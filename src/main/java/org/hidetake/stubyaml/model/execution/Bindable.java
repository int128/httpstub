package org.hidetake.stubyaml.model.execution;

import java.util.Map;

public interface Bindable {

    Map getBinding();

    Map createBinding();

}
