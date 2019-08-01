
#include "I2Cdev.h"
#include "MPU6050_6Axis_MotionApps20.h"
#include <FastLED.h>;

MPU6050 mpu;

#define PIN 6
#define NUM_PIXELS 16 * 16

#define LED_PIN 13 // (Arduino is 13, Teensy is 11, Teensy++ is 6)
bool blinkState = false;

// MPU control/status vars
bool dmpReady = false;  // set true if DMP init was successful
uint8_t mpuIntStatus;   // holds actual interrupt status byte from MPU
uint8_t devStatus;      // return status after each device operation (0 = success, !0 = error)
uint16_t packetSize;    // expected DMP packet size (default is 42 bytes)
uint16_t fifoCount;     // count of all bytes currently in FIFO
uint8_t fifoBuffer[64]; // FIFO storage buffer


int t = 0;

// orientation/motion vars
Quaternion quat;           // [w, x, y, z]         quaternion container
Quaternion lastQuat;           // [w, x, y, z]         quaternion container
VectorInt16 aa;         // [x, y, z]            accel sensor measurements
VectorInt16 aaReal;     // [x, y, z]            gravity-free accel sensor measurements
VectorInt16 aaWorld;    // [x, y, z]            world-frame accel sensor measurements
VectorFloat gravity;    // [x, y, z]            gravity vector
VectorFloat up;
VectorFloat transformed;
float euler[3];         // [psi, theta, phi]    Euler angle container
float ypr[3];           // [yaw, pitch, roll]   yaw/pitch/roll container and gravity vector

// packet structure for InvenSense teapot demo
//                                      orientation                     color     mode    counter
uint8_t teapotPacket[19] = { '$', 0x02, 0,0, 0,0, 0,0, 0,0, 0x00, 0x00, 0, 0, 0,  0,      0,       '\r', '\n' };
uint8_t color[3] = { 0, 0, 0 };

uint8_t currentColorIndex = 0;

const uint8_t numColors = 2;
uint8_t colors[numColors][4][3] = {
  { { 0, 255, 255 }, { 100, 100, 255 }, { 255, 255, 0 }, { 255, 50, 200 } },
  { { 255, 0, 255 }, { 100, 100, 255 }, { 255, 100, 50 }, { 240, 255, 95 } },
};

uint16_t flipTimer = 0;
uint16_t timeUntilSwitch = 300;
uint8_t mode = 0;
uint8_t lightIndex = 0;

float totalRotation = 0;
float rotationThreshold = 6;

unsigned long previousMillis = 0;        // will store last time LED was updated
const long interval = 60;
bool sendColors = false;

CRGB leds[NUM_PIXELS];

// ================================================================
// ===               INTERRUPT DETECTION ROUTINE                ===
// ================================================================

volatile bool mpuInterrupt = false;     // indicates whether MPU interrupt pin has gone high
void dmpDataReady() {
    mpuInterrupt = true;
}


// ================================================================
// ===                      INITIAL SETUP                       ===
// ================================================================

void setup() {
  up = VectorFloat(0, 0, -1);

  FastLED.addLeds<NEOPIXEL, PIN>(leds, NUM_PIXELS);
  FastLED.setBrightness(100);

  // join I2C bus (I2Cdev library doesn't do this automatically)
  #if I2CDEV_IMPLEMENTATION == I2CDEV_ARDUINO_WIRE
      Wire.begin();
      TWBR = 24; // 400kHz I2C clock (200kHz if CPU is 8MHz)
  #elif I2CDEV_IMPLEMENTATION == I2CDEV_BUILTIN_FASTWIRE
      Fastwire::setup(400, true);
  #endif  

  // initialize serial communication
  // (115200 chosen because it is required for Teapot Demo output, but it's 
  // really up to you depending on your project)
  Serial.begin(57600);
  while (!Serial); // wait for Leonardo enumeration, others continue immediately

  mpu.initialize();

  devStatus = mpu.dmpInitialize();

  mpu.setXGyroOffset(0);
  mpu.setYGyroOffset(0);
  mpu.setZGyroOffset(0);
  mpu.setZAccelOffset(1788); // 1688 factory default for my test chip

  // make sure it worked (returns 0 if so)
  if (devStatus == 0) {
    // turn on the DMP, now that it's ready
    //Serial.println(F("Enabling DMP..."));
    mpu.setDMPEnabled(true);

    // enable Arduino interrupt detection
    //Serial.println(F("Enabling interrupt detection (Arduino external interrupt 0)..."));
    attachInterrupt(0, dmpDataReady, RISING);
    mpuIntStatus = mpu.getIntStatus();

    // set our DMP Ready flag so the main loop() function knows it's okay to use it
    //Serial.println(F("DMP ready! Waiting for first interrupt..."));
    dmpReady = true;

    // get expected DMP packet size for later comparison
    packetSize = mpu.dmpGetFIFOPacketSize();
  } else {
    // ERROR!
  }

  // configure LED for output
  pinMode(LED_PIN, OUTPUT);
}



// ================================================================
// ===                    MAIN PROGRAM LOOP                     ===
// ================================================================

void loop() {
  unsigned long currentMillis = millis();
  
  // if programming failed, don't try to do anything
  if (!dmpReady) return;

  // wait for MPU interrupt or extra packet(s) available
  while (!mpuInterrupt && fifoCount < packetSize) {
      // other program behavior stuff here
      // .
      // .
      // .
      // if you are really paranoid you can frequently test in between other
      // stuff to see if mpuInterrupt is true, and if so, "break;" from the
      // while() loop to immediately process the MPU data
      // .
      // .
      // .
  }

  // reset interrupt flag and get INT_STATUS byte
  mpuInterrupt = false;
  mpuIntStatus = mpu.getIntStatus();

  // get current FIFO count
  fifoCount = mpu.getFIFOCount();

  // check for overflow (this should never happen unless our code is too inefficient)
  if ((mpuIntStatus & 0x10) || fifoCount == 1024) {
      // reset so we can continue cleanly
      mpu.resetFIFO();
      // Serial.println(F("FIFO overflow!"));

  // otherwise, check for DMP data ready interrupt (this should happen frequently)
  } else if (mpuIntStatus & 0x02) {
    // wait for correct available data length, should be a VERY short wait
    while (fifoCount < packetSize) fifoCount = mpu.getFIFOCount();

    // read a packet from FIFO
    mpu.getFIFOBytes(fifoBuffer, packetSize);
    
    // track FIFO count here in case there is > 1 packet available
    // (this lets us immediately read more without waiting for an interrupt)
    fifoCount -= packetSize;

    // display quaternion values in InvenSense Teapot demo format:
    teapotPacket[2] = fifoBuffer[0];
    teapotPacket[3] = fifoBuffer[1];
    teapotPacket[4] = fifoBuffer[4];
    teapotPacket[5] = fifoBuffer[5];
    teapotPacket[6] = fifoBuffer[8];
    teapotPacket[7] = fifoBuffer[9];
    teapotPacket[8] = fifoBuffer[12];
    teapotPacket[9] = fifoBuffer[13];
    // 10 - blank?
    // 11 - counter?
    teapotPacket[12] = color[0];
    teapotPacket[13] = color[1];
    teapotPacket[14] = color[2];
    teapotPacket[15] = mode;
    teapotPacket[16] = ((float) totalRotation / rotationThreshold) * 255;
    Serial.write(teapotPacket, 19);
    teapotPacket[11]++; // packetCount, loops at 0xFF on purpose

    // blink LED to indicate activity
    blinkState = !blinkState;
    digitalWrite(LED_PIN, blinkState);
  }


//  lastQuat.x = quat.x;
//  lastQuat.y = quat.y;
//  lastQuat.z = quat.z;
//  lastQuat.w = quat.w;
//  
//  mpu.dmpGetQuaternion(&quat, fifoBuffer);
//  mpu.dmpGetEuler(euler, &quat);

//  Quaternion diff = Quaternion(
//    quat.w - lastQuat.w,
//    quat.x - lastQuat.x,
//    quat.y - lastQuat.y,
//    quat.z - lastQuat.z);
//  float rotationalVelocity = diff.getMagnitude();
//  
//  transformed = VectorFloat(0, 0, -1);
//  transformed.rotate(&quat);
//
//  float flipAmount = (dot(up, transformed) + 1) / 2;

//  if (mode == 0) {
//    totalRotation += rotationalVelocity;
//
//    // STABLE MODE: transition between two colors as you flip
//    
//    if (totalRotation > rotationThreshold) {
//      // switch to power up mode
//      mode = 1;
//    }
//
//    // stable mode
////    setColor(totalRotation / rotationThreshold);
//  } else if (mode == 1) {
//    if (flipTimer > timeUntilSwitch) {
//      flipTimer = 0;
//      mode = 2;
//      FastLED.clear();
//    }
//    
//    // power up mode
//    uint8_t c = cos8(flipTimer * flipTimer * 0.01f);
//    //uint8_t c = cos8(flipTimer * 10);
//    color[0] = color[1] = color[2] = c;
//
//    flipTimer++;
//
//    colorWipe();
//  } else if (mode == 2) {
//    // TRANSITION MODE: nothing here yet
//    
//    mode = 0;
//    up = VectorFloat(0, 0, up.z * -1);
//    currentColorIndex = (currentColorIndex + 1) % numColors;
//    totalRotation = 0;
//  }


  while (Serial.available() > 0) {
    int mode = Serial.read();
    switch (mode) {
      case 0:
        color[0] = 255;
        color[1] = 0;
        color[2] = 0;
        break;
      case 1:
        color[0] = 255;
        color[1] = 255;
        color[2] = 0;
        break;        
    }
  }

  setStableColor();

//  Serial.println("--------");
//  Serial.println(mode);
//  Serial.println(flipAmount);
//  Serial.println(flipTimer);
//  Serial.println(sin8(flipTimer * flipTimer * 0.05f));
}

float dot(VectorFloat v1, VectorFloat v2) {
  return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
}

float setColor(float flipAmount) {
  uint8_t * primaryColor = colors[currentColorIndex][0];
  uint8_t * secondaryColor = colors[currentColorIndex][2];
  color[0] = primaryColor[0] + (secondaryColor[0] - primaryColor[0]) * flipAmount;
  color[1] = primaryColor[1] + (secondaryColor[1] - primaryColor[1]) * flipAmount;
  color[2] = primaryColor[2] + (secondaryColor[2] - primaryColor[2]) * flipAmount;
}

void setStableColor() {
  unsigned long currentMillis = millis();

  if (currentMillis - previousMillis > interval) {
//    uint8_t * primaryColor1 = colors[currentColorIndex][0];
//    uint8_t * primaryColor2 = colors[currentColorIndex][1];
//    uint8_t * secondaryColor1 = colors[currentColorIndex][2];
//    uint8_t * secondaryColor2 = colors[currentColorIndex][3];
//
//    uint8_t color1[3];
//    uint8_t color2[3];
//    uint8_t finalColor[3];
//
//     interpolate(flipAmount, primaryColor1, secondaryColor1, color1);
//     interpolate(flipAmount, primaryColor2, secondaryColor2, color2);

    previousMillis = currentMillis; 

    int offset = currentMillis / 5.0;
    
    for (int i = 0; i < NUM_PIXELS; i++) {
//       float interpolation = cos8(i * 5 + offset) / 255.0;
//       interpolate(interpolation, color1, color2, finalColor);
      leds[i].setRGB(color[0], color[1], color[2]);
    }
  
    FastLED.show();
  }
}

void interpolate(float amount, uint8_t * a, uint8_t * b, uint8_t * result) {
  result[0] = a[0] + (b[0] - a[0]) * amount;
  result[1] = a[1] + (b[1] - a[1]) * amount;
  result[2] = a[2] + (b[2] - a[2]) * amount;
}

void colorWipe() {
  unsigned long currentMillis = millis();

  if (currentMillis - previousMillis > interval) {
    previousMillis = currentMillis;
    
    for (int i = 0; i < NUM_PIXELS; i++) {
      uint8_t v = cos8((float) i * 10 + flipTimer * flipTimer / 15);
      leds[i].setRGB(v, v, v);
    }
  
    FastLED.show();
  }
}
