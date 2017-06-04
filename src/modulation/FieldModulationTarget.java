package modulation;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldModulationTarget implements ModulationTarget {
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
	public Type getType() {
		return field.getType();
	}

	@Override
	public String getName() {
		return field.getName();
	}

	@Override
	public Mod getModAnnotation() {
		return field.getAnnotation(Mod.class);
	}

	public Object getValue() {
		try {
			return field.get(parent);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
