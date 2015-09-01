

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ru.budetsdelano.startup.entities.ConfigurationClassField;
import ru.budetsdelano.startup.server.dao.ConfigurationClassFieldService;

import java.lang.reflect.Field;

@Component
public class DatabaseBeanConfigurator implements BeanPostProcessor{
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private ConfigurationClassFieldService configurationClassFieldService;
	private static final Logger logger = Logger.getLogger(BeanPostProcessor.class);
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		logger.debug("bean class is "+bean.getClass().getCanonicalName());
		for (Field field : bean.getClass().getFields()) {
			ConfigurationClassField configurationClassField = configurationClassFieldService.findByClassAndField(bean.getClass().getCanonicalName(), field.getName());
			if (configurationClassField != null) {
				if (configurationClassField.getBigDecimalValue() != null) {
					field.setAccessible(true);
					try {
						field.set(bean, configurationClassField.getBigDecimalValue());
					} catch (IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
				else if (configurationClassField.getIntValue() != null) {
					field.setAccessible(true);
					try {
						field.set(bean, configurationClassField.getIntValue());
					} catch (IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
				else if (configurationClassField.getStringValue() != null) {
					field.setAccessible(true);
					try {
						field.set(bean, configurationClassField.getStringValue());
					} catch (IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
				else if (configurationClassField.getBooleanValue() != null) {
					field.setAccessible(true);
					try {
						field.set(bean, configurationClassField.getBooleanValue());
					} catch (IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return bean;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		for (Field field : bean.getClass().getFields()) {
			ConfigurationClassField configurationClassField = configurationClassFieldService.findByClassAndField(bean.getClass().getCanonicalName(), field.getName());
			if (configurationClassField != null) {
				if (configurationClassField.getBigDecimalValue() != null) {
					field.setAccessible(true);
					try {
						field.set(bean, configurationClassField.getBigDecimalValue());
					} catch (IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
				else if (configurationClassField.getIntValue() != null) {
					field.setAccessible(true);
					try {
						field.set(bean, configurationClassField.getIntValue());
					} catch (IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
				else if (configurationClassField.getStringValue() != null) {
					field.setAccessible(true);
					try {
						field.set(bean, configurationClassField.getStringValue());
					} catch (IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
				else if (configurationClassField.getBooleanValue() != null) {
					field.setAccessible(true);
					try {
						field.set(bean, configurationClassField.getBooleanValue());
					} catch (IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return bean;
	}

}
