package com.projectnocturne.sensortag;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.projectnocturne.NocturneApplication;
import com.projectnocturne.sensortag.constants.TiBleConstants;

import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT8;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;
import static java.lang.Math.pow;

/**
 * This enum encapsulates the differences amongst the sensors. The differences
 * include UUID values and how to interpret the
 * characteristic-containing-measurement.
 */
public enum Sensor {
	IR_TEMPERATURE(java.util.UUID.fromString(TiBleConstants.IRTEMPERATURE_SERV_UUID), java.util.UUID
			.fromString(TiBleConstants.IRTEMPERATURE_DATA_UUID), java.util.UUID
			.fromString(TiBleConstants.IRTEMPERATURE_CONF_UUID)) {

		private double extractAmbientTemperature(BluetoothGattCharacteristic c) {
			int offset = 2;
			return shortUnsignedAtOffset(c, offset) / 128.0;
		}

		private double extractTargetTemperature(BluetoothGattCharacteristic c, double ambient) {
			Integer twoByteValue = shortSignedAtOffset(c, 0);

			double Vobj2 = twoByteValue.doubleValue();
			Vobj2 *= 0.00000015625;

			double Tdie = ambient + 273.15;

			double S0 = 5.593E-14; // Calibration factor
			double a1 = 1.75E-3;
			double a2 = -1.678E-5;
			double b0 = -2.94E-5;
			double b1 = -5.7E-7;
			double b2 = 4.63E-9;
			double c2 = 13.4;
			double Tref = 298.15;
			double S = S0 * (1 + a1 * (Tdie - Tref) + a2 * pow((Tdie - Tref), 2));
			double Vos = b0 + b1 * (Tdie - Tref) + b2 * pow((Tdie - Tref), 2);
			double fObj = (Vobj2 - Vos) + c2 * pow((Vobj2 - Vos), 2);
			double tObj = pow(pow(Tdie, 4) + (fObj / S), .25);

			return tObj - 273.15;
		}

		@Override
		public void onCharacteristicChanged(BluetoothGattCharacteristic c) {

			/*
			 * The IR Temperature sensor produces two measurements; Object ( AKA
			 * target or IR) Temperature, and Ambient ( AKA die ) temperature.
			 *
			 * Both need some conversion, and Object temperature is dependent on
			 * Ambient temperature.
			 *
			 * They are stored as [ObjLSB, ObjMSB, AmbLSB, AmbMSB] (4 bytes)
			 * Which means we need to shift the bytes around to get the correct
			 * values.
			 */

			double ambient = extractAmbientTemperature(c);
			double target = extractTargetTemperature(c, ambient);

			Log.w(NocturneApplication.LOG_TAG, LOG_TAG + "onServicesDiscovered() [ambient: " + ambient + "] [target:"
					+ target + "]");

			// model.setAmbientTemperature(ambient);
			// model.setTargetTemperature(target);
		}
	},

	ACCELEROMETER(java.util.UUID.fromString(TiBleConstants.ACCELEROMETER_SERV_UUID), java.util.UUID
			.fromString(TiBleConstants.ACCELEROMETER_DATA_UUID), java.util.UUID
			.fromString(TiBleConstants.ACCELEROMETER_CONF_UUID)) {
		@Override
		public void onCharacteristicChanged(final BluetoothGattCharacteristic c) {
			/*
			 * The accelerometer has the range [-2g, 2g] with unit (1/64)g.
			 *
			 * To convert from unit (1/64)g to unit g we divide by 64.
			 *
			 * (g = 9.81 m/s^2)
			 *
			 * The z value is multiplied with -1 to coincide with how we have
			 * arbitrarily defined the positive y direction. (illustrated by the
			 * apps accelerometer image)
			 */

			Integer x = c.getIntValue(FORMAT_SINT8, 0);
			Integer y = c.getIntValue(FORMAT_SINT8, 1);
			Integer z = c.getIntValue(FORMAT_SINT8, 2) * -1;

			double scaledX = x / 64.0;
			double scaledY = y / 64.0;
			double scaledZ = z / 64.0;

			Log.w(NocturneApplication.LOG_TAG, LOG_TAG + "onServicesDiscovered() Accelerometer [scaledX: " + scaledX
					+ "] [scaledY:" + scaledY + "] [scaledZ:" + scaledZ + "]");

			// model.setAccelerometer(scaledX, scaledY, scaledZ);
		}
	},

	HUMIDITY(java.util.UUID.fromString(TiBleConstants.HUMIDITY_SERV_UUID), java.util.UUID
			.fromString(TiBleConstants.HUMIDITY_DATA_UUID), java.util.UUID
			.fromString(TiBleConstants.HUMIDITY_CONF_UUID)) {
		@Override
		public void onCharacteristicChanged(BluetoothGattCharacteristic c) {
			int a = shortUnsignedAtOffset(c, 2);
			// bits [1..0] are status bits and need to be cleared according
			// to the userguide, but the iOS code doesn't bother. It should
			// have minimal impact.
			a = a - (a % 4);

			Log.w(NocturneApplication.LOG_TAG, LOG_TAG + "onServicesDiscovered() [Humidity: "
					+ ((-6f) + 125f * (a / 65535f)) + "]");

			// model.setHumidity((-6f) + 125f * (a / 65535f));
		}
	},

	MAGNETOMETER(java.util.UUID.fromString(TiBleConstants.MAGNETOMETER_SERV_UUID), java.util.UUID
			.fromString(TiBleConstants.MAGNETOMETER_DATA_UUID), java.util.UUID
			.fromString(TiBleConstants.MAGNETOMETER_CONF_UUID)) {
		@Override
		public void onCharacteristicChanged(BluetoothGattCharacteristic c) {
			// Multiply x and y with -1 so that the values correspond with our
			// pretty
			// pictures in the app.
			float x = shortSignedAtOffset(c, 0) * (2000f / 65536f) * -1;
			float y = shortSignedAtOffset(c, 2) * (2000f / 65536f) * -1;
			float z = shortSignedAtOffset(c, 4) * (2000f / 65536f);

			Log.w(NocturneApplication.LOG_TAG, LOG_TAG + "onServicesDiscovered() Magnetometer [x: " + x + "] [y: " + y
					+ "] [z: " + z + "]");

			// model.setMagnetometer(x, y, z);
		}
	},

	GYROSCOPE(java.util.UUID.fromString(TiBleConstants.GYROSCOPE_SERV_UUID), java.util.UUID
			.fromString(TiBleConstants.GYROSCOPE_DATA_UUID), java.util.UUID
			.fromString(TiBleConstants.GYROSCOPE_CONF_UUID), new byte[] { 7 }) {
		@Override
		public void onCharacteristicChanged(BluetoothGattCharacteristic c) {
			// NB: x,y,z has a weird order.
			float y = shortSignedAtOffset(c, 0) * (500f / 65536f) * -1;
			float x = shortSignedAtOffset(c, 2) * (500f / 65536f);
			float z = shortSignedAtOffset(c, 4) * (500f / 65536f);

			Log.w(NocturneApplication.LOG_TAG, LOG_TAG + "onServicesDiscovered() Gyroscope [x: " + x + "] [y: " + y
					+ "] [z: " + z + "]");

			// model.setGyroscope(x, y, z);
		}
	},

	BAROMETER(java.util.UUID.fromString(TiBleConstants.GYROSCOPE_SERV_UUID), java.util.UUID
			.fromString(TiBleConstants.GYROSCOPE_DATA_UUID), java.util.UUID
			.fromString(TiBleConstants.GYROSCOPE_CONF_UUID)) {
		@Override
		public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic) {

			List<Integer> barometerCalibrationCoefficients = BarometerCalibrationCoefficients.INSTANCE.barometerCalibrationCoefficients;
			if (barometerCalibrationCoefficients == null) {
				Log.w("Custom", "Data notification arrived for barometer before it was calibrated.");
				return;
			}

			final int[] c; // Calibration coefficients
			final Integer t_r; // Temperature raw value from sensor
			final Integer p_r; // Pressure raw value from sensor
			final Double S; // Interim value in calculation
			final Double O; // Interim value in calculation
			final Double p_a; // Pressure actual value in unit Pascal.

			c = new int[barometerCalibrationCoefficients.size()];
			for (int i = 0; i < barometerCalibrationCoefficients.size(); i++) {
				c[i] = barometerCalibrationCoefficients.get(i);
			}

			t_r = shortSignedAtOffset(characteristic, 0);
			p_r = shortUnsignedAtOffset(characteristic, 2);

			S = c[2] + c[3] * t_r / pow(2, 17) + ((c[4] * t_r / pow(2, 15)) * t_r) / pow(2, 19);
			O = c[5] * pow(2, 14) + c[6] * t_r / pow(2, 3) + ((c[7] * t_r / pow(2, 15)) * t_r) / pow(2, 4);
			p_a = (S * p_r + O) / pow(2, 14);

			Log.w(NocturneApplication.LOG_TAG, LOG_TAG + "onServicesDiscovered() Barometer [p_a: " + p_a + "]");

			// model.setBarometer(p_a);
		}
	},

	SIMPLE_KEYS(java.util.UUID.fromString(TiBleConstants.SIMPLE_KEYS_SERV_UUID), java.util.UUID
			.fromString(TiBleConstants.SIMPLE_KEYS_DATA_UUID), null, null) {
		@Override
		public void onCharacteristicChanged(BluetoothGattCharacteristic c) {
			/*
			 * The key state is encoded into 1 unsigned byte. bit 0 designates
			 * the right key. bit 1 designates the left key. bit 2 designates
			 * the side key.
			 *
			 * Weird, in the userguide left and right are opposite.
			 */
			Integer encodedInteger = c.getIntValue(FORMAT_UINT8, 0);

			SimpleKeysStatus newValue = SimpleKeysStatus.values()[encodedInteger % 4];
			Log.w(NocturneApplication.LOG_TAG, LOG_TAG + "onServicesDiscovered() Keys [val: " + newValue + "]");

			// model.setSimpleKeysStatus(newValue);
		}
	};

	public static Sensor getFromDataUuid(UUID uuid) {
		for (Sensor s : Sensor.values()) {
			if (s.getData().equals(uuid)) { return s; }
		}
		throw new RuntimeException("Programmer error, unable to find uuid.");
	}

	/**
	 * Gyroscope, Magnetometer, Barometer, IR temperature all store 16 bit two's
	 * complement values in the awkward format LSB MSB, which cannot be directly
	 * parsed as getIntValue(FORMAT_SINT16, offset) because the bytes are stored
	 * in the "wrong" direction.
	 *
	 * This function extracts these 16 bit two's complement values.
	 * */
	private static Integer shortSignedAtOffset(BluetoothGattCharacteristic c, int offset) {
		Integer lowerByte = c.getIntValue(FORMAT_UINT8, offset);
		Integer upperByte = c.getIntValue(FORMAT_SINT8, offset + 1); // Note:
																	 // interpret
																	 // MSB as
																	 // signed.

		return (upperByte << 8) + lowerByte;
	}

	private static Integer shortUnsignedAtOffset(BluetoothGattCharacteristic c, int offset) {
		Integer lowerByte = c.getIntValue(FORMAT_UINT8, offset);
		Integer upperByte = c.getIntValue(FORMAT_UINT8, offset + 1); // Note:
																	 // interpret
																	 // MSB as
																	 // unsigned.

		return (upperByte << 8) + lowerByte;
	}

	private final UUID service, data, config;
	private byte[] enableCode; // See getEnableSensorCode for explanation.

	/**
	 * Our Model in our MVC-structured code, the sensors update the model when
	 * new data arrives. The sensors are therefore the Controllers in our MVC.
	 *
	 * Note: Having the variable "model" is only done for brevity reasons.
	 * */
	// private final static Measurements model = Measurements.INSTANCE;

	public static final String LOG_TAG = "Sensor" + "::";

	/**
	 * Constructor called by all the sensors except Gyroscope.
	 * */
	private Sensor(UUID service, UUID data, UUID config) {
		this.service = service;
		this.data = data;
		this.config = config;
		this.enableCode = new byte[] { 1 }; // This is the sensor enable code
											// used
											// for almost all sensors. (Not
											// Gyroscope)
	}

	/**
	 * Constructor called by the gryroscope because he needs a different enable
	 * code.
	 */
	private Sensor(UUID service, UUID data, UUID config, byte[] enableCode) {
		this.service = service;
		this.data = data;
		this.config = config;
		this.enableCode = enableCode;
	}

	public UUID getConfig() {
		return config;
	}

	public UUID getData() {
		return data;
	}

	/**
	 * @return the code which, when written to the configuration characteristic,
	 *         turns on the sensor.
	 * */
	public byte[] getEnableSensorCode() {
		return enableCode;
	}

	public UUID getService() {
		return service;
	}

	public void onCharacteristicChanged(BluetoothGattCharacteristic c) {
		throw new UnsupportedOperationException(
				"Programmer error, the individual enum classes are supposed to override this method.");
	}
}
