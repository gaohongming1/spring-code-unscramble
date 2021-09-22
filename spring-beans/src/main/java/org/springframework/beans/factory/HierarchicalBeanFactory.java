/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory;

/**
 * Sub-interface implemented by bean factories that can be part
 * of a hierarchy.
 *
 * <p>The corresponding {@code setParentBeanFactory} method for bean
 * factories that allow setting the parent in a configurable
 * fashion can be found in the ConfigurableBeanFactory interface.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 07.07.2003
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#setParentBeanFactory
 */

/**
 * 提供分层的bean，spring的父子容器之间是隔离的，父容器不能访问子容器中的bean但是子容器可以访问父容器中的bean
 * 引入父子容器主要是拥有分层概念比如service层不能注入controller层的bean
 * 注意父子容器中可以存在同名的bean，获取bean的时候会以此向上查找
 */
public interface HierarchicalBeanFactory extends org.springframework.beans.factory.BeanFactory {

	/**
	 * Return the parent bean factory, or {@code null} if there is none.
	 * 返回此容器的父容器
	 */
	org.springframework.beans.factory.BeanFactory getParentBeanFactory();

	/**
	 * Return whether the local bean factory contains a bean of the given name,
	 * ignoring beans defined in ancestor contexts.
	 * <p>This is an alternative to {@code containsBean}, ignoring a bean
	 * of the given name from an ancestor bean factory.
	 * @param name the name of the bean to query
	 * @return whether a bean with the given name is defined in the local factory
	 * @see BeanFactory#containsBean
	 *判断当前容器是否存在这个bean，不会去查找父容器
	 */
	boolean containsLocalBean(String name);

}
