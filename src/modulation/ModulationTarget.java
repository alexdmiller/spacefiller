package modulation;

import java.lang.reflect.Type;

public interface ModulationTarget {
	void setValue(Object object);
	Type getType();
	String getName();
	Mod getModAnnotation();
}
