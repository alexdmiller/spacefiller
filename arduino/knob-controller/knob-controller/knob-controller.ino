#define KNOB_COUNT 6

// header character, knobs, and button state
byte packet [KNOB_COUNT + 2];

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9000);          //  setup serial
}

void loop() {
  // Serial.write(0);

  Serial.println("Hello world");
  
//  packet[0] = 255;
//  for (int i = 0; i < KNOB_COUNT; i++) {
//    int value = min(analogRead(i) / 4, 254);    // read the input pin
//    packet[i + 1] = value;
//  }
//
//  packet[KNOB_COUNT + 1] = analogRead(KNOB_COUNT) == 1023 ? 1 : 0;
//
//  Serial.write(packet, KNOB_COUNT + 2);
//
//  int b = analogRead(KNOB_COUNT);
//  Serial.println(b);

  delay(50);
}
