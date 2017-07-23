package modulation;

import com.sun.javaws.exceptions.InvalidArgumentException;
import modulation.FieldModulationTarget;
import modulation.MethodModulationTarget;
import modulation.Mod;
import modulation.ModulationTarget;
import oscP5.*;

import javax.json.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class OscSceneModulator implements OscEventListener {
	public static final boolean DEBUG = false;

	private Map<String, ModulationTarget> modulationTargetRegistry;
	private OscP5 oscP5;
	private String name;

	private static Map<Type, Integer> typeToVDMXType = new HashMap<>();
	private static Map<Type, VDMXElementClass> typeToVDMXElementClass = new HashMap<>();

	static {
		typeToVDMXType.put(Integer.TYPE, 2);
		typeToVDMXType.put(Float.TYPE, 4);
		typeToVDMXType.put(Boolean.TYPE, 1);
		typeToVDMXType.put(null, 1);

		typeToVDMXElementClass.put(Integer.TYPE, VDMXElementClass.SLIDER);
		typeToVDMXElementClass.put(Float.TYPE, VDMXElementClass.SLIDER);
		typeToVDMXElementClass.put(Boolean.TYPE, VDMXElementClass.BUTTON);
		typeToVDMXElementClass.put(null, VDMXElementClass.BUTTON);
	}

	private enum VDMXElementClass {
		BUTTON("Button"), SLIDER("Slider");

		String name;

		VDMXElementClass(String name) {
			this.name = name;
		}
	}

	public OscSceneModulator(Object object, int port) {
		oscP5 = new OscP5(this, port);
		modulationTargetRegistry = new TreeMap<>();
		registerTargetsForObject(object, "");
		name = object.getClass().getName();
		exportVDMXJson(port);
	}

	private void registerTargetsForObject(Object object, String targetPath) {
		// TODO: Address shouldn't just be class name -- it should also depend on nesting as well
		for (Field field : object.getClass().getFields()) {
			if (field.isAnnotationPresent(Mod.class)) {
				if (field.getType().isPrimitive()) {
					Mod mod = field.getAnnotation(Mod.class);
					String address = mod.address().isEmpty()
							? targetPath + "/" + field.getName()
							: mod.address();
					modulationTargetRegistry.put(address, new FieldModulationTarget(object, field));
				} else {
					try {
						registerTargetsForObject(field.get(object), targetPath + "/" + field.getName());
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (Method method : object.getClass().getMethods()) {
			if (method.isAnnotationPresent(Mod.class)) {
				Mod mod = method.getAnnotation(Mod.class);
				String address = mod.address().isEmpty()
						? "/" + object.getClass().getName() + "/" + method.getName()
						: mod.address();
				System.out.println(address);
				modulationTargetRegistry.put(address, new MethodModulationTarget(object, method));
			}
		}
	}

	private void exportVDMXJson(int port) {
		JsonArrayBuilder classes = Json.createArrayBuilder();
		JsonArrayBuilder keys = Json.createArrayBuilder();
		JsonObjectBuilder uiBuilder = Json.createObjectBuilder();

		for (String address : modulationTargetRegistry.keySet()) {
			addUiElement(classes, keys, uiBuilder, modulationTargetRegistry.get(address), address, port);
		}

		JsonObject layout = Json.createObjectBuilder()
				.add("classArray", classes)
				.add("keyArray", keys)
				.add("uiBuilder", uiBuilder)
				.build();

		File file = new File(name + ".json");
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		JsonWriter writer = Json.createWriter(out);
		writer.write(layout);
	}

	private void addUiElement(
			JsonArrayBuilder classes,
			JsonArrayBuilder keys,
			JsonObjectBuilder uiBuilder,
			ModulationTarget target,
			String address,
			int port) {
		Mod mod = target.getModAnnotation();
		VDMXElementClass elementClass = typeToVDMXElementClass.get(target.getType());

		classes.add(elementClass.name);
		keys.add(target.getName());

		float val = 0;
		if (target instanceof FieldModulationTarget) {
			FieldModulationTarget fieldModulationTarget = (FieldModulationTarget) target;

			if (fieldModulationTarget.getValue() instanceof Integer) {
				val = ((Integer) fieldModulationTarget.getValue()).floatValue();
			} else {
				val = (float) fieldModulationTarget.getValue();
			}
		} else {
			val = target.getModAnnotation().defaultValue();
		}

		// Scale value so that it's between 0 and 1
		val = (val - mod.min()) / (mod.max() - mod.min());

		uiBuilder.add(target.getName(), Json.createObjectBuilder()
				.add("minRange", mod.min())
				.add("maxRange", mod.max())
				.add("val", val)
				.add("label", address)
				.add("publishNorm", false)
				.add("sendEnabled", true)
				.add("toggle", false)
				.add("sndrs", Json.createArrayBuilder()
						.add(Json.createObjectBuilder()
								.add("msgAddress", address)
								.add("normalizeFlag", false)
								.add("floatInvertFlag", false)
								.add("outputPort", port)
								.add("boolInvertFlag", false)
								.add("boolThreshVal", 0.1)
								.add("lowIntVal", mod.min())
								.add("highIntVal", mod.max())
								.add("outputIPAddress", "127.0.0.1")
								.add("outputLabel", "localhost:" + port)
								.add("dataSenderType", 2)
								.add("enabled", true)
								.add("intInvertFlag", false)
								.add("senderType", typeToVDMXType.get(target.getType())))).build());
	}

	@Override
	public void oscEvent(OscMessage oscMessage) {
		ModulationTarget target = modulationTargetRegistry.get(oscMessage.addrPattern());
		Type type = target.getType();
		OscArgument argument = oscMessage.get(0);

		if (type == Float.TYPE) {
			target.setValue(argument.floatValue());

			if (DEBUG) {
				System.out.println(oscMessage.addrPattern() + ' ' + type + ' ' + argument.floatValue());
			}
		} else if (type == Integer.TYPE) {
			target.setValue(argument.intValue());

			if (DEBUG) {
				System.out.println(oscMessage.addrPattern() + ' ' + type + ' ' + argument.intValue());
			}
		} else if (type == Boolean.TYPE) {
			// VDMX sends boolean values by setting the type of the message to 'T' or 'F'. Because
			// this is non-standard, it has to be handled manually.
			byte b = oscMessage.getTypetagAsBytes()[0];
			target.setValue(b == 'T');

			if (DEBUG) {
				System.out.println(oscMessage.addrPattern() + ' ' + type + ' ' + b);
			}
		} else if (type == null) {
			// A modulation target with a null type is a method with no parameters. We should treat
			// the same way as a boolean modulation target, but only call the method when the boolean
			// is true.
			byte b = oscMessage.getTypetagAsBytes()[0];
			if (b == 'T') {
				target.setValue(null);
			}

			if (DEBUG) {
				System.out.println(oscMessage.addrPattern() + " method");
			}
		}
	}

	@Override
	public void oscStatus(OscStatus oscStatus) {

	}

}
