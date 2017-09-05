package common;

import org.omg.CORBA.INTERNAL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller on 9/3/17.
 */
public class Integrators {
  private List<Integrator> integratorList;

  public Integrators() {
    integratorList = new ArrayList<>();
  }

  public Integrator create() {
    Integrator integrator = new Integrator();
    integratorList.add(integrator);
    return integrator;
  }

  public void update() {
    for (Integrator integrator : integratorList) {
      integrator.update();
    }
  }
}
