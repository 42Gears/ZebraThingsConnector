# ZebraThingsConnector
Things Connector For Zebra Android Touch Computer

## Zebra Smart Battery
Zebra SDK exposes several properies present in a Touch Computer. Details of the APIs in Zebra EMDK is available here: https://techdocs.zebra.com/emdk-for-android/latest/guide/reference/refbatteryintent/

Zebra Battry Thing contains read-only properties. To create a Thing Object:
```java
final ZebraBatteryThing batteryThing = new ZebraBatteryThing("Zebra SmartBattery", batteryInfo);
```
To add a string property in Zebra Thing:
```java
        final Value<String> propValue = new Value<String>(initialValue);
        final Map<String, Object> propMap = new HashMap<String, Object>();
        propMap.put("@type", propName);
        propMap.put("label", label);
        propMap.put("type", "Integer");
        propMap.put("readOnly", true);
        propMap.put("description", label);

        thing.addProperty(new Property<String>(thing, propName, propValue, propMap));
```
To update the value of an existing property:
```java
propertyObject.notifyOfExternalUpdate(newValaueOfProperty);
```

## Zebra Inbuilt Scanner
Zebra Scanner contains several configurable properties (read-write properties) and also actions that can be taken on the scanner.

To add a read-write property:
```java
        final Value<Boolean> propValue = new Value<Boolean>(initialValue, f -> callback(f));
        final Map<String, Object> propMap = new HashMap<String, Object>();
        propMap.put("@type", propName);
        propMap.put("label", label);
        propMap.put("type", "boolean");
        propMap.put("readOnly", false);
        propMap.put("description", label);

        thing.addProperty(new Property<Boolean>(thing, propName, propValue, propMap));
```
`callback(f)` is a consumer function where f represent the new value of the property.

To add an action class:
```java
    public static class MyAction extends Action {
        @Override
        public void performAction() {
            // perfrom your action here //
        }
    }
```
        
