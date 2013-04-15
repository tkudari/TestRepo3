package com.dashwire.config.launcher;

import android.content.ComponentName;
import android.content.Context;
import com.dashwire.base.debug.DashLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AppWidgetFactory {

	private static final AppWidgetFactory factory = new AppWidgetFactory();

	private static final String APP_WIDGET = "app_widget";

	private static final String HTC_WIDGET = "htc_widget";

	private static final String HTC_FX_WIDGET = "htc_fx_widget";

	private AppWidgetFactory() {
	}

	public synchronized static AppWidgetFactory getInstance(Context context) {
		return factory;
	}

    /**
     * Creates an ItemInfo based on the provided information.
     *
     * @param oemType "aw" or "htcw"
     * @param cn componentName of widget
     * @param uri URI of component
     * @return an ItemInfo implementation
     */
	public ItemInfo createItemInfo(String oemType, ComponentName cn,
			String uri) {
		
		/**
		 * We don't have concept of 3d widget in dashconfig. Let's just try to
		 * infer it here...
		 */
		String itemType = ("aw".equals(oemType) || uri == null) ? APP_WIDGET : HTC_FX_WIDGET;

		ItemInfo info = null;
		if (itemType.equals(HTC_WIDGET)) {
			info = new HtcWidgetInfo(cn, getWidgetName(uri, cn));
		} else if (itemType.equals(HTC_FX_WIDGET)) {
			info = new HtcFxWidgetInfo(cn, getWidgetName(uri, cn));
		} else {
			info = new AppWidgetInfo(cn);
		}

		if(info != null) {
			info.itemTypeName = itemType;
		}
		return info;
	}
	
	private static String getWidgetName(String uri, ComponentName cn) {
		DashLogger.d("Factory", "Uri = " + uri);
		if(uri == null || !uri.contains("/")) {
			return null;
		}
		String widgetName = uri.split("[/]")[1];
		return widgetName.startsWith(".") ? cn.getPackageName() + widgetName
				: widgetName;
	}

	private static void setPackageNameAttributeOn(Element element,
			String packageName) {
		element.setAttribute(SceneSettings.Favorites.PACKAGE_NAME, packageName);
	}

	private static void setWidgetNameAttributeOn(Element element,
			ComponentName componentName, String widgetName) {
		element.setAttribute(
				SceneSettings.Favorites.WIDGET_NAME,
				widgetName.startsWith(".") ? (componentName.getPackageName() + widgetName)
						: widgetName);

	}

	private static void setProviderNameAttributeOn(Element element,
			ComponentName componentName) {
		element.setAttribute(
				SceneSettings.Favorites.PROVIDER_NAME,
				componentName.getShortClassName().startsWith(".") ? componentName.getPackageName()
						+ componentName.getShortClassName() : componentName.getShortClassName());
	}

	private static void setClassNameAttributeOn(Element element,
			ComponentName componentName) {
		element.setAttribute(
				SceneSettings.Favorites.CLASS_NAME,
				componentName.getShortClassName().startsWith(".") ? componentName.getPackageName()
						+ componentName.getShortClassName() : componentName.getShortClassName());
	}

	private static class HtcFxWidgetInfo extends ItemInfo {

		ComponentName componentName;

		String widgetName;

		HtcFxWidgetInfo(ComponentName componentName, String widgetName) {
			if(componentName == null) {
				throw new IllegalArgumentException("componentName: null");
			}
			
			if(widgetName == null) {
				throw new IllegalArgumentException("widgetName: null, CN = " 
						+ componentName.flattenToString());
				
			}
			this.componentName = componentName;
			this.widgetName = widgetName;
		}

		@Override
		public Element asDomElement(Document document) {
			Element element = super.asDomElement(document);
			setPackageNameAttributeOn(element, componentName.getPackageName());
			setProviderNameAttributeOn(element, componentName);
			setWidgetNameAttributeOn(element, componentName, widgetName);
			return element;
		}
	}

	private static class HtcWidgetInfo extends ItemInfo {

		ComponentName componentName;

		String widgetName;

		HtcWidgetInfo(ComponentName componentName, String widgetName) {
			this.componentName = componentName;
			this.widgetName = widgetName;
			if(componentName == null) {
				throw new IllegalArgumentException("componentName: null");
			}
			
			if(widgetName == null) {
				throw new IllegalArgumentException("widgetName: null");
			}
		}

		@Override
		public Element asDomElement(Document document) {
			Element element = super.asDomElement(document);
			setPackageNameAttributeOn(element, componentName.getPackageName());
			setWidgetNameAttributeOn(element, componentName, widgetName);

			return element;
		}
	}

	private static class AppWidgetInfo extends ItemInfo {

		ComponentName componentName;

		AppWidgetInfo(ComponentName componentName) {
			this.componentName = componentName;
			if(componentName == null) {
				throw new IllegalArgumentException("componentName: null");
			}
		}

		@Override
		public Element asDomElement(Document document) {
			Element element = super.asDomElement(document);
			setPackageNameAttributeOn(element, componentName.getPackageName());
			setClassNameAttributeOn(element, componentName);
			return element;
		}
	}
	
}
