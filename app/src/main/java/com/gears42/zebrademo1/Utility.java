package com.gears42.zebrademo1;

import org.mozilla.iot.webthing.Property;
import org.mozilla.iot.webthing.Thing;
import org.mozilla.iot.webthing.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Utility {

    /**
     * Creates an Integer property
     **/
    public static Value<Integer> getIntegerProperty(final Thing thing, final String propName, final String label,
                                                    final boolean readOnly, final int initialValue) {

        final Value<Integer> propValue = new Value<Integer>(initialValue);
        final Map<String, Object> propMap = new HashMap<String, Object>();
        propMap.put("@type", propName);
        propMap.put("label", label);
        propMap.put("type", "Integer");
        propMap.put("readOnly", readOnly);
        propMap.put("description", label);

        thing.addProperty(new Property<Integer>(thing, propName, propValue, propMap));
        return propValue;
    }

    /**
     * Creates a String property
     **/
    public static Value<String> getStringProperty(final Thing thing, final String propName, final String label,
                                                  final boolean readOnly, final String initialValue) {

        final Value<String> propValue = new Value<String>(initialValue);
        final Map<String, Object> propMap = new HashMap<String, Object>();
        propMap.put("@type", propName);
        propMap.put("label", label);
        propMap.put("type", "Integer");
        propMap.put("readOnly", readOnly);
        propMap.put("description", label);

        thing.addProperty(new Property<String>(thing, propName, propValue, propMap));
        return propValue;
    }

    /**
     * Creates a Boolean property
     **/
    public static Value<Boolean> getBooleanProperty(final Thing thing, final String propName, final String label,
                                                    final boolean readOnly, final boolean initialValue) {

        final Value<Boolean> propValue = new Value<Boolean>(initialValue);
        final Map<String, Object> propMap = new HashMap<String, Object>();
        propMap.put("@type", propName);
        propMap.put("label", label);
        propMap.put("type", "boolean");
        propMap.put("readOnly", readOnly);
        propMap.put("description", label);

        thing.addProperty(new Property<Boolean>(thing, propName, propValue, propMap));
        return propValue;
    }

    /**
     * Creates a modifiable Boolean property
     **/
    public static Value<Boolean> getBooleanProperty(final Thing thing, final String propName, final String label,
                                                    final boolean readOnly, final boolean initialValue, final Consumer<Boolean> valueForwarder) {

        final Value<Boolean> propValue = new Value<Boolean>(initialValue, valueForwarder);
        final Map<String, Object> propMap = new HashMap<String, Object>();
        propMap.put("@type", propName);
        propMap.put("label", label);
        propMap.put("type", "boolean");
        propMap.put("readOnly", readOnly);
        propMap.put("description", label);

        thing.addProperty(new Property<Boolean>(thing, propName, propValue, propMap));
        return propValue;
    }

    /**
     * Converts seconds into days, hours, minutes and seconds
     **/
    public static String formatSeconds(final int seconds) {
        long duration = seconds;

        final long days = TimeUnit.SECONDS.toDays(duration);
        duration -= TimeUnit.DAYS.toSeconds(days);

        final long hours = TimeUnit.SECONDS.toHours(duration);
        duration -= TimeUnit.HOURS.toSeconds(hours);

        final long minutes = TimeUnit.SECONDS.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toSeconds(minutes);

        final long secs = TimeUnit.SECONDS.toSeconds(duration);
        final StringBuilder msg = new StringBuilder();
        if (days != 0) {
            msg.append(days + " day" + (days > 1 ? "s " : " "));
        }
        if (hours != 0) {
            msg.append(hours + " hour" + (hours > 1 ? "s " : " "));
        }
        if (minutes != 0) {
            msg.append(minutes + " minute" + (minutes > 1 ? "s " : " "));
        }
        if (secs != 0) {
            msg.append(secs + " second" + (seconds > 1 ? "s " : " "));
        }
        return msg.toString();
    }

    /**
     * Compares two nullable strings
     **/
    public static boolean equals(final String str1, final String str2) {
        if (str1 == null && str2 == null) {
            return true;
        } else if (str1 == null || str2 == null) {
            return false;
        } else {
            return str1.equals(str2);
        }
    }

    /**
     * Sleep without interruption
     **/
    public static boolean sleep(long millis) {
        try {
            Thread.sleep(millis);
            return false;
        } catch (InterruptedException e) {
            return true;
        }
    }
}
