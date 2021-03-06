package org.pcu.connectors.storage;

/*-
 * #%L
 * PCU Storage Core
 * %%
 * Copyright (C) 2017 - 2019 PCU Consortium
 * %%
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
 * #L%
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PcuStorageFactory {

	public static PcuStorage createStorage(PcuStorageConfiguration configuration) {

		if (configuration.getConfigutation() == null || configuration.getClassName() == null) {
			throw new IllegalArgumentException("configuration invalid");
		}

		Class<?> clazz;
		try {
			clazz = Class.forName(configuration.getClassName());
		} catch (Exception e) {
			throw new IllegalArgumentException("storage class invalid");
		}

		Constructor<?> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(PcuStorageConfiguration.class);
		} catch (Exception e) {
			throw new IllegalArgumentException("could not find valid constructor");
		}

		Object pcuStorageInstance;
		try {
			pcuStorageInstance = constructor.newInstance(configuration);
		} catch (InvocationTargetException ite) {
			if (ite.getCause() != null) {
				throw new IllegalArgumentException(
						"instanciation of class thrown an exception : " + ite.getCause().getMessage(),
						ite.getTargetException());
			} else {
				throw new IllegalArgumentException("instanciation of class thrown an exception",
						ite.getTargetException());
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new IllegalArgumentException("could not instanciate class");
		}
		return (PcuStorage) pcuStorageInstance;

	}
}
