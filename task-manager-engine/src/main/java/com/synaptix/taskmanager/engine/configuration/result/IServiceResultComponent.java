package com.synaptix.taskmanager.engine.configuration.result;

import com.synaptix.component.IComponent;

/**
 * Service Result as a component to use synaptix proxies, processors, ...
 *
 * @author Nicolas P
 */
public interface IServiceResultComponent<O extends Object> extends IComponent, IServiceWithErrorResult<O> {

}
