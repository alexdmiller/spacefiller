#define SENSOR_COUNT 7 // 6 knobs + 1 button

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);          //  setup serial
  while (!Serial) {
    ;
  }
}

void loop() {
  for (int i = 0; i < SENSOR_COUNT; i++) {
    Serial.print(analogRead(i));
    Serial.print(",");
  }
  Serial.println();
}
