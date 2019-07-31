
//#include "I2Cdev.h"
#include "MPU6050_6Axis_MotionApps20.h"
#include <FastLED.h>;

MPU6050 mpu;

#define PINA 7
#define PINB 6
#define PINC 5
//#define NUM_PIXELS 36 * 3
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
uint8_t teapotPacket[19] = { '$', 0x02, 0, 0, 0, 0, 0, 0, 0, 0, 0x00, 0x00, 0, 0, 0,  0,      0,       '\r', '\n' };
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

  FastLED.addLeds<NEOPIXEL, PINA >(leds, NUM_PIXELS);
  FastLED.setBrightness(40);
  FastLED.addLeds<NEOPIXEL, PINB >(leds, NUM_PIXELS);
  FastLED.setBrightness(40);
  FastLED.addLeds<NEOPIXEL, PINC >(leds, NUM_PIXELS);
  FastLED.setBrightness(40);
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
    Serial.write(teapotPacket, 19);
    
    teapotPacket[11]++; // packetCount, loops at 0xFF on purpose

    // blink LED to indicate activity
    blinkState = !blinkState;
    digitalWrite(LED_PIN, blinkState);
  }


  lastQuat.x = quat.x;
  lastQuat.y = quat.y;
  lastQuat.z = quat.z;
  lastQuat.w = quat.w;

  mpu.dmpGetQuaternion(&quat, fifoBuffer);
  mpu.dmpGetEuler(euler, &quat);

  Quaternion diff = Quaternion(
                      quat.w - lastQuat.w,
                      quat.x - lastQuat.x,
                      quat.y - lastQuat.y,
                      quat.z - lastQuat.z);
  float rotationalVelocity = diff.getMagnitude();

  transformed = VectorFloat(0, 0, -1);
  transformed.rotate(&quat);

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

  for (int i = 0; i < NUM_PIXELS; i++) {
    //       float interpolation = cos8(i * 5 + offset) / 255.0;
    //       interpolate(interpolation, color1, color2, finalColor);
    leds[i].setRGB(color[0], color[1], color[2]);
  }

  FastLED.show();
}

float dot(VectorFloat v1, VectorFloat v2) {
  return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
}
