package modulation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodModulationTarget implements ModulationTarget {
	private Object parent;
	private Method method;

	public MethodModulationTarget(Object parent, Method method) {
		this.parent = parent;
		this.method = method;
	}

	@Override
	public void setValue(Object value) {
		try {
			if (method.getParameterCount() > 0) {
				method.invoke(parent, value);
			} else {
				method.invoke(parent);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
		}
	}

	@Override
	public Type getType() {
		if (method.getParameterCount() > 0) {
			return method.getParameterTypes()[0];
		} else {
			return null;
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
