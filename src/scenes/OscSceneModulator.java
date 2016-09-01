package scenes;

import oscP5.OscEventListener;
import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscStatus;

import javax.json.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

public class OscSceneModulator implements OscEventListener {
	private Map<String, ModulationTarget> modulationTargetRegistry;
	private OscP5 oscP5;
	private String name;

	public OscSceneModulator(Object object, int port) {
		oscP5 = new OscP5(this, port);
		oscP5.addListener(this);
		modulationTargetRegistry = new HashMap<>();
		registerTargetsForObject(object);
		name = object.getClass().getName();
		exportVDMXJson(port);
	}

	private void registerTargetsForObject(Object object) {
		for (Field field : object.getClass().getFields()) {
			if (field.isAnnotationPresent(Mod.class)) {
				if (field.getType().isPrimitive()) {
					modulationTargetRegistry.put("/" + object.getClass().getName() + "/" + field.getName(), new FieldModulationTarget(object, field));
				} else {
					try {
						registerTargetsForObject(field.get(object));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (Method method : object.getClass().getMethods()) {
			if (method.isAnnotationPresent(Mod.class)) {
				modulationTargetRegistry.put("/" + object.getClass().getName() + "/" + method.getName(), new MethodModulationTarget(object, method));
			}
		}
	}

	private void exportVDMXJson(int port) {
		JsonArrayBuilder classes = Json.createArrayBuilder();
		JsonArrayBuilder keys = Json.createArrayBuilder();
		JsonObjectBuilder uiBuilder = Json.createObjectBuilder();

		for (String address : modulationTargetRegistry.keySet()) {
			addSlider(classes, keys, uiBuilder, modulationTargetRegistry.get(address), address, port);
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

	private void addSlider(
			JsonArrayBuilder classes,
			JsonArrayBuilder keys,
			JsonObjectBuilder uiBuilder,
			ModulationTarget target,
			String address,
			int port) {
		Mod mod = target.getModAnnotation();

		classes.add("Slider");
		keys.add(target.getName());

		uiBuilder.add(target.getName(), Json.createObjectBuilder()
				.add("minRange", mod.min())
				.add("maxRange", mod.max())
				.add("val", mod.defaultValue())
				.add("label", target.getName())
				.add("publishNorm", false)
				.add("sendEnabled", true)
				.add("sndrs", Json.createArrayBuilder()
						.add(Json.createObjectBuilder()
								.add("msgAddress", address)
								.add("normalizeFlag", false)
								.add("floatInvertFlag", false)
								.add("outputPort", port)
								.add("boolInvertFlag", false)
								.add("boolThreshVal", 0.1)
								.add("highIntVal", 100)
								.add("outputIPAddress", "127.0.0.1")
								.add("outputLabel", "localhost:" + port)
								.add("dataSenderType", 2)
								.add("lowIntVal", 0)
								.add("enabled", true)
								.add("intInvertFlag", false)
								.add("senderType", 4))).build());
	}

	@Override
	public void oscEvent(OscMessage oscMessage) {
		System.out.println(oscMessage);
		System.out.println(oscMessage.get(0).floatValue());

		ModulationTarget target = modulationTargetRegistry.get(oscMessage.addrPattern());
		if (target != null) {
			target.setValue(oscMessage.get(0).floatValue());
		}
	}

	@Override
	public void oscStatus(OscStatus oscStatus) {

	}

	private interface ModulationTarget {
		void setValue(Object object);
		String getName();
		Mod getModAnnotation();
	}

	private static class FieldModulationTarget implements ModulationTarget {
		private Object parent;
		private Field field;

		public FieldModulationTarget(Object parent, Field field) {
			this.parent = parent;
			this.field = field;
		}

		@Override
		public void setValue(Object value) {
			try {
				field.set(parent, value);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		@Override
		public String getName() {
			return field.getName();
		}

		@Override
		public Mod getModAnnotation() {
			return field.getAnnotation(Mod.class);
		}
	}

	private static class MethodModulationTarget implements ModulationTarget {
		private Object parent;
		private Method method;

		public MethodModulationTarget(Object parent, Method method) {
			this.parent = parent;
			this.method = method;
		}

		@Override
		public void setValue(Object value) {
			try {
				method.invoke(parent, value);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
			}
		}

		@Override
		public String getName() {
			return method.getName();
		}

		@Override
		public Mod getModAnnotation() {
			return method.getAnnotation(Mod.class);
		}
	}
}
