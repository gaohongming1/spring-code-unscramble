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

package org.springframework.beans;

import org.springframework.core.AttributeAccessorSupport;

/**
 * Extension of {@link org.springframework.core.AttributeAccessorSupport},
 * holding attributes as {@link BeanMetadataAttribute} objects in order
 * to keep track of the definition source.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
@SuppressWarnings("serial")

//主要用于Bean的元数据和属性上下文操作的实现类
public class BeanMetadataAttributeAccessor extends AttributeAccessorSupport implements org.springframework.beans.BeanMetadataElement {

	private Object source;


	/**
	 * Set the configuration source {@code Object} for this metadata element.
	 * <p>The exact type of the object will depend on the configuration mechanism used.
	 *
	 */
	public void setSource(Object source) {
		this.source = source;
	}

	@Override
	public Object getSource() {
		return this.source;
	}


	/**
	 * Add the given BeanMetadataAttribute to this accessor's set of attributes.
	 * @param attribute the BeanMetadataAttribute object to register
	 *将给定的BeanMetadataAttribute 添加到属性集也就是attributes中
	 *
	 */
	public void addMetadataAttribute(org.springframework.beans.BeanMetadataAttribute attribute) {
		super.setAttribute(attribute.getName(), attribute);
	}

	/**
	 * Look up the given BeanMetadataAttribute in this accessor's set of attributes.
	 * @param name the name of the attribute
	 * @return the corresponding BeanMetadataAttribute object,
	 * or {@code null} if no such attribute defined
	 * 从属性集中找到属性上下文
	 */
	public org.springframework.beans.BeanMetadataAttribute getMetadataAttribute(String name) {
		return (org.springframework.beans.BeanMetadataAttribute) super.getAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		super.setAttribute(name, new org.springframework.beans.BeanMetadataAttribute(name, value));
	}

	//获得属性的属性值
	@Override
	public Object getAttribute(String name) {
		org.springframework.beans.BeanMetadataAttribute attribute = (org.springframework.beans.BeanMetadataAttribute) super.getAttribute(name);
		return (attribute != null ? attribute.getValue() : null);
	}

	//根据属性名去除属性值 并返回取出后的属性值
	@Override
	public Object removeAttribute(String name) {
		org.springframework.beans.BeanMetadataAttribute attribute = (org.springframework.beans.BeanMetadataAttribute) super.removeAttribute(name);
		return (attribute != null ? attribute.getValue() : null);
	}

}
