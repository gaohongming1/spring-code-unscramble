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

package org.springframework.beans.factory.config;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringValueResolver;

/**
 * Configuration interface to be implemented by most bean factories. Provides
 * facilities to configure a bean factory, in addition to the bean factory
 * client methods in the {@link org.springframework.beans.factory.BeanFactory}
 * interface.
 *
 * <p>This bean factory interface is not meant to be used in normal application
 * code: Stick to {@link org.springframework.beans.factory.BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * needs. This extended interface is just meant to allow for framework-internal
 * plug'n'play and for special access to bean factory configuration methods.
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.ListableBeanFactory
 * @see ConfigurableListableBeanFactory
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, org.springframework.beans.factory.config.SingletonBeanRegistry {

	/**
	 * 单例模式 标识符
	 */
	String SCOPE_SINGLETON = "singleton";

	/**
	 * 原型模式标识符
	 */
	String SCOPE_PROTOTYPE = "prototype";


	/**
	 * Set the parent of this bean factory.
	 * <p>Note that the parent cannot be changed: It should only be set outside
	 * a constructor if it isn't available at the time of factory instantiation.
	 * @param parentBeanFactory the parent BeanFactory
	 * @throws IllegalStateException if this factory is already associated with
	 * a parent BeanFactory
	 * @see #getParentBeanFactory()
	 *
	 * 设置父容器 注意一旦设置后就不再被更改
	 * 搭配HierarchicalBeanfactory 的getParentBeanFactory 使用
	 */
	void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

	/**
	 * Set the class loader to use for loading bean classes.
	 * Default is the thread context class loader.
	 * <p>Note注意 that this class loader will only apply to bean definitions
	 * that do not carry a resolved bean class yet. This is the case as of
	 * Spring 2.0 by default: Bean definitions only carry bean class names,
	 * to be resolved once the factory processes the bean definition.
	 * @param beanClassLoader the class loader to use,
	 * or {@code null} to suggest the default class loader
	 * 设置类加载器 默认使用线程上下文的加载器
	 * 这个类加载器只会用来夹在 bean
	 */
	void setBeanClassLoader(ClassLoader beanClassLoader);

	/**
	 * Return this factory's class loader for loading bean classes.
	 * 返回当前工厂的类加载器
	 */
	ClassLoader getBeanClassLoader();

	/**
	 * Specify a temporary ClassLoader to use for type matching purposes.
	 * Default is none, simply using the standard bean ClassLoader.
	 * <p>A temporary ClassLoader is usually just specified if
	 * <i>load-time weaving</i> is involved, to make sure that actual bean
	 * classes are loaded as lazily as possible. The temporary loader is
	 * then removed once the BeanFactory completes its bootstrap phase.
	 * @since 2.5
	 * 设置临时的类加载器 为了类型匹配 默认是null
	 * 当BeanFactory完成引导 临时加载器会被移除
	 */
	void setTempClassLoader(ClassLoader tempClassLoader);

	/**
	 * Return the temporary ClassLoader to use for type matching purposes,
	 * if any.
	 * @since 2.5
	 * 返回临时类加载器
	 */
	ClassLoader getTempClassLoader();

	/**
	 * Set whether to cache bean metadata such as given bean definitions
	 * (in merged fashion) and resolved bean classes. Default is on.
	 * <p>Turn this flag off to enable hot-refreshing of bean definition objects
	 * and in particular bean classes. If this flag is off, any creation of a bean
	 * instance will re-query the bean class loader for newly resolved classes.
	 * 是否需要缓存bean的元数据 比如BeanDefination  加载好的classes
	 * 默认是开启状态
	 * 关闭这个值意思是开启bean的热刷新机制
	 * 任何创建bean都会重新请求类加载器重新加载创建bean实例
	 */
	void setCacheBeanMetadata(boolean cacheBeanMetadata);

	/**
	 * Return whether to cache bean metadata such as given bean definitions
	 * (in merged fashion) and resolved bean classes.
	 * 返回是否开启Bean的热刷新机制
	 */
	boolean isCacheBeanMetadata();

	/**
	 * Specify the resolution strategy for expressions in bean definition values.
	 * <p>There is no expression support active in a BeanFactory by default.
	 * An ApplicationContext will typically set a standard expression strategy
	 * here, supporting "#{...}" expressions in a Unified EL compatible style.
	 * @since 3.0
	 * 启用表达式支持
	 * 设置BeanFactory的表达式支持
	 * 一般来说BeanFactory没有设置表达式支持 但是ApplicationContext会设置一个标准的表达式 通过这个方法 支持EL表达式
	 * 用来解析生成bean definition
	 */
	void setBeanExpressionResolver(org.springframework.beans.factory.config.BeanExpressionResolver resolver);

	/**
	 * Return the resolution strategy for expressions in bean definition values.
	 * @since 3.0
	 * 获得当前工厂的Bean表达式
	 */
	org.springframework.beans.factory.config.BeanExpressionResolver getBeanExpressionResolver();

	/**
	 * Specify a Spring 3.0 ConversionService to use for converting
	 * property values, as an alternative to JavaBeans PropertyEditors.
	 * @since 3.0
	 * 设置和获取Conversion接口实现类型转换
	 * 设置Spring的数据转换  比如Controller层的接收参数 序列化和反序列化都会用到这个
	 * 或者读取到的配置文件中的内容 转换为Java中的对象
	 */
	void setConversionService(ConversionService conversionService);

	/**
	 * Return the associated ConversionService, if any.
	 * @since 3.0
	 * 返回转换服务
	 */
	ConversionService getConversionService();

	/**
	 * Add a PropertyEditorRegistrar to be applied to all bean creation processes.
	 * <p>Such a registrar creates new PropertyEditor instances and registers them
	 * on the given registry, fresh for each bean creation attempt. This avoids
	 * the need for synchronization on custom editors; hence, it is generally
	 * preferable to use this method instead of {@link #registerCustomEditor}.
	 * @param registrar the PropertyEditorRegistrar to register
	 * 添加应用于所有Bean 创建过程中的   PropertyEditorRegistrar
	 * 使用参考：https://blog.csdn.net/qq_43414291/article/details/111226055
	 * 解析配置文件到Java类中
	 */
	void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

	/**
	 * Register the given custom property editor for all properties of the
	 * given type. To be invoked during factory configuration.
	 * <p>Note that this method will register a shared custom editor instance;
	 * access to that instance will be synchronized for thread-safety. It is
	 * generally preferable to use {@link #addPropertyEditorRegistrar} instead
	 * of this method, to avoid for the need for synchronization on custom editors.
	 * @param requiredType type of the property
	 * @param propertyEditorClass the {@link PropertyEditor} class to register
	 * 添加指定类型的bean 的属性编辑器，这个只会应用于特定类型
	 * 这个方法会注册一个共享的编辑器实例  访问这个编辑器实例是同步的线程安全的
	 * 通常最好使用上面那个方法替代
	 * 这个方法是为了避免在自定义类型编辑器上使用同步
	 */
	void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

	/**
	 * Initialize the given PropertyEditorRegistry with the custom editors
	 * that have been registered with this BeanFactory.
	 * @param registry the PropertyEditorRegistry to initialize
	 * 用工厂中已经注册的通用编辑器初始化指定的属性 编辑器
	 */
	void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

	/**
	 * Set a custom type converter that this BeanFactory should use for converting
	 * bean property values, constructor argument values, etc.
	 * <p>This will override the default PropertyEditor mechanism and hence make
	 * any custom editors or custom editor registrars irrelevant.
	 * @see #addPropertyEditorRegistrar
	 * @see #registerCustomEditor
	 * @since 2.5
	 * 设置beanFactory用来转换bean属性值或者参数值的自定义转换器
	 * 会覆盖默认的 PropertyEditor
	 */
	void setTypeConverter(TypeConverter typeConverter);

	/**
	 * Obtain a type converter as used by this BeanFactory. This may be a fresh
	 * instance for each call, since TypeConverters are usually <i>not</i> thread-safe.
	 * <p>If the default PropertyEditor mechanism is active, the returned
	 * TypeConverter will be aware of all custom editors that have been registered.
	 * @since 2.5
	 * 获得当前beanfactory的转换器，每次请求都可能是新的实例
	 * 因为类型转换器通常是线程安全的
	 */
	TypeConverter getTypeConverter();

	/**
	 * Add a String resolver for embedded values such as annotation attributes.
	 * @param valueResolver the String resolver to apply to embedded values
	 * @since 3.0
	 * 为嵌入值添加一个 StringValueResolver
	 *
	 */
	void addEmbeddedValueResolver(StringValueResolver valueResolver);

	/**
	 * Resolve the given embedded value, e.g. an annotation attribute.
	 * @param value the value to resolve
	 * @return the resolved value (may be the original value as-is)
	 * @since 3.0
	 * 分解指定的表达式的值
	 */
	String resolveEmbeddedValue(String value);

	/**
	 * Add a new BeanPostProcessor that will get applied to beans created
	 * by this factory. To be invoked during factory configuration.
	 * <p>Note: Post-processors submitted here will be applied in the order of
	 * registration; any ordering semantics expressed through implementing the
	 * {@link org.springframework.core.Ordered} interface will be ignored. Note
	 * that autodetected post-processors (e.g. as beans in an ApplicationContext)
	 * will always be applied after programmatically registered ones.
	 * @param beanPostProcessor the post-processor to register
	 * 添加一个新的BeanPostProcessor 用来增强bean的初始功能
	 * 设置后置处理器
	 * 使用示例 	 https://blog.csdn.net/geekjoker/article/details/79868945
	 */
	void addBeanPostProcessor(org.springframework.beans.factory.config.BeanPostProcessor beanPostProcessor);

	/**
	 * Return the current number of registered BeanPostProcessors, if any.
	 * 返回当前的后置处理器
	 */
	int getBeanPostProcessorCount();

	/**
	 * Register the given scope, backed by the given Scope implementation.
	 * @param scopeName the scope identifier
	 * @param scope the backing Scope implementation
	 * 注册一个作用域
	 * 使用示例：https://www.cnblogs.com/feixy/p/5870233.html
	 *
	 */
	void registerScope(String scopeName, org.springframework.beans.factory.config.Scope scope);

	/**
	 * Return the names of all currently registered scopes.
	 * <p>This will only return the names of explicitly registered scopes.
	 * Built-in scopes such as "singleton" and "prototype" won't be exposed.
	 * @return the array of scope names, or an empty array if none
	 * @see #registerScope
	 * 返回已经显示注册的所有的作用域，内置的作用域比如singleton prototype 不会返回
	 */
	String[] getRegisteredScopeNames();

	/**
	 * Return the Scope implementation for the given scope name, if any.
	 * <p>This will only return explicitly registered scopes.
	 * Built-in scopes such as "singleton" and "prototype" won't be exposed.
	 * @param scopeName the name of the scope
	 * @return the registered Scope implementation, or {@code null} if none
	 * @see #registerScope
	 * 返回给定名称作用域的实现 这里只会返回注册的不会返回内置的
	 */
	org.springframework.beans.factory.config.Scope getRegisteredScope(String scopeName);

	/**
	 * Provides a security access control context relevant to this factory.
	 * @return the applicable AccessControlContext (never {@code null})
	 * @since 3.0
	 * 提供当前beanFactory的访问控制的上下文
	 */
	AccessControlContext getAccessControlContext();

	/**
	 * Copy all relevant configuration from the given other factory.
	 * <p>Should include all standard configuration settings as well as
	 * BeanPostProcessors, Scopes, and factory-specific internal settings.
	 * Should not include any metadata of actual bean definitions,
	 * such as BeanDefinition objects and bean name aliases.
	 * @param otherFactory the other BeanFactory to copy from
	 * 从另一个Bean工厂 copy相关的配置，比如   BeanPostProcessors 作用域 等其他设定
	 * 不会包含Bean的元数据信息
	 */
	void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

	/**
	 * Given a bean name, create an alias. We typically use this method to
	 * support names that are illegal within XML ids (used for bean names).
	 * <p>Typically invoked during factory configuration, but can also be
	 * used for runtime registration of aliases. Therefore, a factory
	 * implementation should synchronize alias access.
	 * @param beanName the canonical name of the target bean
	 * @param alias the alias to be registered for the bean
	 * @throws BeanDefinitionStoreException if the alias is already in use
	 * 给定一个bean注册别名
	 * 通常使用这个方法注册在xml中非法的别名
	 * 同时也能用在运行时的别名注册，因此，bean工厂对bean名称的访问应该是同步的线程安全的
	 */
	void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;

	/**
	 * Resolve all alias target names and aliases registered in this
	 * factory, applying the given StringValueResolver to them.
	 * <p>The value resolver may for example resolve placeholders
	 * in target bean names and even in alias names.
	 * @param valueResolver the StringValueResolver to apply
	 * @since 2.5
	 * 根据指定的StringValueResolver 移除相关的别名
	 */
	void resolveAliases(StringValueResolver valueResolver);

	/**
	 * Return a merged BeanDefinition for the given bean name,
	 * merging a child bean definition with its parent if necessary.
	 * Considers bean definitions in ancestor factories as well.
	 * @param beanName the name of the bean to retrieve the merged definition for
	 * @return a (potentially merged) BeanDefinition for the given bean
	 * @throws NoSuchBeanDefinitionException if there is no bean definition with the given name
	 * @since 2.5
	 * 返回一个合并的beanDefinition的定义
	 * 比如父子bean 以及父容器中的定义
	 */
	org.springframework.beans.factory.config.BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Determine whether the bean with the given name is a FactoryBean.
	 * @param name the name of the bean to check
	 * @return whether the bean is a FactoryBean
	 * ({@code false} means the bean exists but is not a FactoryBean)
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.5
	 * 判断给定的bean名称是不是工厂bean
	 */
	boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Explicitly control the current in-creation status of the specified bean.
	 * For container-internal use only.
	 * @param beanName the name of the bean
	 * @param inCreation whether the bean is currently in creation
	 * @since 3.1
	 * 在ioc容器内部使用的 显示的指定一个bean为正在创建状态
	 * 解决循环依赖时候使用
	 */
	void setCurrentlyInCreation(String beanName, boolean inCreation);

	/**
	 * Determine whether the specified bean is currently in creation.
	 * @param beanName the name of the bean
	 * @return whether the bean is currently in creation
	 * @since 2.5
	 * 返回指定的bean是否正在创建中
	 * 这里也是在解决循环依赖时候砽
	 */
	boolean isCurrentlyInCreation(String beanName);

	/**
	 * Register a dependent bean for the given bean,
	 * to be destroyed before the given bean is destroyed.
	 * @param beanName the name of the bean
	 * @param dependentBeanName the name of the dependent bean
	 * @since 2.5
	 * 创建一个依赖与指定bean的bean，处理bean依赖问题使用
	 * 在给定的bean销毁之前将这个bean销毁
	 */
	void registerDependentBean(String beanName, String dependentBeanName);

	/**
	 * Return the names of all beans which depend on the specified bean, if any.
	 * @param beanName the name of the bean
	 * @return the array of dependent bean names, or an empty array if none
	 * @since 2.5
	 * 返回需要依赖的bean的名称
	 */
	String[] getDependentBeans(String beanName);

	/**
	 * Return the names of all beans that the specified bean depends on, if any.
	 * @param beanName the name of the bean
	 * @return the array of names of beans which the bean depends on,
	 * or an empty array if none
	 * @since 2.5
	 * 返回已经依赖的bean的名称
	 */
	String[] getDependenciesForBean(String beanName);

	/**
	 * Destroy the given bean instance (usually a prototype instance
	 * obtained from this factory) according to its bean definition.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * @param beanName the name of the bean definition
	 * @param beanInstance the bean instance to destroy
	 * 销毁一个bean 通常是原型模式使用
	 */
	void destroyBean(String beanName, Object beanInstance);

	/**
	 * Destroy the specified scoped bean in the current target scope, if any.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * @param beanName the name of the scoped bean
	 * 销毁当前作用域的指定的bean名称
	 */
	void destroyScopedBean(String beanName);

	/**
	 * Destroy all singleton beans in this factory, including inner beans that have
	 * been registered as disposable. To be called on shutdown of a factory.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * 销毁该工厂所有的单例bean
	 */
	void destroySingletons();

}
