/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;

/**
 * A BeanDefinition describes a bean instance, which has property values,
 * constructor argument values, and further information supplied by
 * concrete implementations.
 * 描述一个bean的实例包括属性值、构造方法、实现的实例
 *
 * <p>This is just a minimal interface: The main intention is to allow a
 * {@link BeanFactoryPostProcessor} such as {@link PropertyPlaceholderConfigurer}
 * to introspect and modify property values and other bean metadata.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 19.03.2004
 * @see ConfigurableListableBeanFactory#getBeanDefinition
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

	/**
	 * Scope identifier for the standard singleton scope: "singleton".
	 * <p>Note that extended bean factories might support further scopes.
	 * @see #setScope
	 * 单例模式值
	 */
	String SCOPE_SINGLETON = org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

	/**
	 * Scope identifier for the standard prototype scope: "prototype".
	 * <p>Note that extended bean factories might support further scopes.
	 * @see #setScope
	 * 原型模式值
	 */
	String SCOPE_PROTOTYPE = org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;


	/**
	 * Role hint indicating that a {@code BeanDefinition} is a major part
	 * of the application. Typically corresponds to a user-defined bean.
	 * 标明一个beanDefinition在application中的角色
	 * 这个是代表用户定义的bean
	 */
	int ROLE_APPLICATION = 0;

	/**
	 * Role hint indicating that a {@code BeanDefinition} is a supporting
	 * part of some larger configuration, typically an outer
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
	 * {@code SUPPORT} beans are considered important enough to be aware
	 * of when looking more closely at a particular
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition},
	 * but not when looking at the overall configuration of an application.
	 * 这个标表标明是外部框架的bean
	 */
	int ROLE_SUPPORT = 1;

	/**
	 * Role hint indicating that a {@code BeanDefinition} is providing an
	 * entirely background role and has no relevance to the end-user. This hint is
	 * used when registering beans that are completely part of the internal workings
	 * of a {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
	 * 标明是容器内部的bean
	 */
	int ROLE_INFRASTRUCTURE = 2;


	/**
	 * Return the name of the parent definition of this bean definition, if any.
	 * 返回父类beanDefinition的名称
	 */
	String getParentName();

	/**
	 * Set the name of the parent definition of this bean definition, if any.
	 * 设置父beanDefinition的名称
	 */
	void setParentName(String parentName);

	/**
	 * Return the current bean class name of this bean definition.
	 * <p>Note that this does not have to be the actual class name used at runtime, in
	 * case of a child definition overriding/inheriting the class name from its parent.
	 * Hence, do <i>not</i> consider this to be the definitive bean type at runtime but
	 * rather only use it for parsing purposes at the individual bean definition level.
	 * 返回当前beanDefinition的class名称
	 */
	String getBeanClassName();

	/**
	 * Override the bean class name of this bean definition.
	 * <p>The class name can be modified during bean factory post-processing,
	 * typically replacing the original class name with a parsed variant of it.
	 * 设置当前beanDefinition的class名称
	 * 这里是覆盖原先的名称的，可以再后置处理中被更改
	 */
	void setBeanClassName(String beanClassName);

	/**
	 * Return the factory bean name, if any.
	 * 获得factoryBean的名称
	 */
	String getFactoryBeanName();

	/**
	 * Specify the factory bean to use, if any.
	 * 设置当前bean的工厂bean
	 */
	void setFactoryBeanName(String factoryBeanName);

	/**
	 * Return a factory method, if any.
	 * 返回工厂bean的工厂方法
	 */
	String getFactoryMethodName();

	/**
	 * Specify a factory method, if any. This method will be invoked with
	 * constructor arguments, or with no arguments if none are specified.
	 * The method will be invoked on the specified factory bean, if any,
	 * or otherwise as a static method on the local bean class.
	 * @param factoryMethodName static factory method name,
	 * or {@code null} if normal constructor creation should be used
	 * @see #getBeanClassName()
	 * 设置工厂bean的方法
	 * 这个方法会调用构造方法，在指定的bean工厂带哦用
	 */
	void setFactoryMethodName(String factoryMethodName);

	/**
	 * Return the name of the current target scope for this bean,
	 * or {@code null} if not known yet.
	 * 返回当前bean的作用域
	 */
	String getScope();

	/**
	 * Override the target scope of this bean, specifying a new scope name.
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 * 设置当前bean的作用域
	 */
	void setScope(String scope);

	/**
	 * Return whether this bean should be lazily initialized, i.e. not
	 * eagerly instantiated on startup. Only applicable to a singleton bean.
	 * 返回当前bean是否是懒加载模式
	 */
	boolean isLazyInit();

	/**
	 * Set whether this bean should be lazily initialized.
	 * <p>If {@code false}, the bean will get instantiated on startup by bean
	 * factories that perform eager initialization of singletons.
	 * 设置当前bean是否是懒加载模式
	 */
	void setLazyInit(boolean lazyInit);

	/**
	 * Return the bean names that this bean depends on.
	 * 返回这个bean所依赖的bean的名称
	 */
	String[] getDependsOn();

	/**
	 * Set the names of the beans that this bean depends on being initialized.
	 * The bean factory will guarantee that these beans get initialized first.
	 * 设置这个bean所依赖的bian的名称
	 */
	void setDependsOn(String[] dependsOn);

	/**
	 * Return whether this bean is a candidate for getting autowired into some other bean.
	 * 返回这个bean是否是其他bean的注入的候选
	 */
	boolean isAutowireCandidate();

	/**
	 * Set whether this bean is a candidate for getting autowired into some other bean.
	 * 设置这个bean是否是作为其他bean属性注入的候选
	 */
	void setAutowireCandidate(boolean autowireCandidate);

	/**
	 * Return whether this bean is a primary autowire candidate.
	 * If this value is true for exactly one bean among multiple
	 * matching candidates, it will serve as a tie-breaker.
	 * 返回这个bean是否是主要的备选者
	 */
	boolean isPrimary();

	/**
	 * Set whether this bean is a primary autowire candidate.
	 * <p>If this value is true for exactly one bean among multiple
	 * matching candidates, it will serve as a tie-breaker.
	 * 设置这个bean是否是主要的备选者
	 */
	void setPrimary(boolean primary);


	/**
	 * Return the constructor argument values for this bean.
	 * <p>The returned instance can be modified during bean factory post-processing.
	 * @return the ConstructorArgumentValues object (never {@code null})
	 * 返回当前bean构造函数的参数值，这些参数值实例可以在post-processing阶段被更改
	 */
	org.springframework.beans.factory.config.ConstructorArgumentValues getConstructorArgumentValues();

	/**
	 * Return the property values to be applied to a new instance of the bean.
	 * <p>The returned instance can be modified during bean factory post-processing.
	 * @return the MutablePropertyValues object (never {@code null})
	 * 返回要应用在新实例上的bean的属性值 这些属性值可以在post-processing阶段被更改
	 */
	MutablePropertyValues getPropertyValues();


	/**
	 * Return whether this a <b>Singleton</b>, with a single, shared instance
	 * returned on all calls.
	 * @see #SCOPE_SINGLETON
	 * 返回是否是单例模式
	 */
	boolean isSingleton();

	/**
	 * Return whether this a <b>Prototype</b>, with an independent instance
	 * returned for each call.
	 * @see #SCOPE_PROTOTYPE
	 * 返回时否是原型模式
	 */
	boolean isPrototype();

	/**
	 * Return whether this bean is "abstract", that is, not meant to be instantiated.
	 * 返回当前bean是否是抽象的也就是不会被实例化的
	 */
	boolean isAbstract();

	/**
	 * Get the role hint for this {@code BeanDefinition}. The role hint
	 * provides the frameworks as well as tools with an indication of
	 * the role and importance of a particular {@code BeanDefinition}.
	 * @see #ROLE_APPLICATION
	 * @see #ROLE_SUPPORT
	 * @see #ROLE_INFRASTRUCTURE
	 * 返回当前bean的角色定位
	 */
	int getRole();

	/**
	 * Return a human-readable description of this bean definition.
	 * 返回这个bean的定义的描述
	 */
	String getDescription();

	/**
	 * Return a description of the resource that this bean definition
	 * came from (for the purpose of showing context in case of errors).
	 * 返回这个bean所在的资源位置
	 */
	String getResourceDescription();

	/**
	 * Return the originating BeanDefinition, or {@code null} if none.
	 * Allows for retrieving the decorated bean definition, if any.
	 * <p>Note that this method returns the immediate originator. Iterate through the
	 * originator chain to find the original BeanDefinition as defined by the user.
	 * 返回原始的BeanDefinition，允许检索装饰的bean，返回用户定义的原始bean
	 */
	BeanDefinition getOriginatingBeanDefinition();

}
