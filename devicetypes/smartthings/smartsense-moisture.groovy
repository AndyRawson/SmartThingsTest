metadata {
	// Automatically generated. Make future change here.
	definition (name: "SmartSense Moisture", namespace: "smartthings", author: "SmartThings") {
		capability "Water Sensor"
		capability "Sensor"
		capability "Battery"

		fingerprint deviceId: "0x2001", inClusters: "0x30,0x9C,0x9D,0x85,0x80,0x72,0x31,0x84,0x86"
		fingerprint deviceId: "0x2101", inClusters: "0x71,0x70,0x85,0x80,0x72,0x31,0x84,0x86"
	}

	simulator {
		status "dry": "command: 7105, payload: 00 00 00 FF 05 FE 00 00"
		status "wet": "command: 7105, payload: 00 FF 00 FF 05 02 00 00"
		status "overheated": "command: 7105, payload: 00 00 00 FF 04 02 00 00"
		status "freezing": "command: 7105, payload: 00 00 00 FF 04 05 00 00"
		status "normal": "command: 7105, payload: 00 00 00 FF 04 FE 00 00"
		for (int i = 0; i <= 100; i += 20) {
			status "battery ${i}%": new physicalgraph.zwave.Zwave().batteryV1.batteryReport(batteryLevel: i).incomingMessage()
		}
	}
	tiles {
		standardTile("water", "device.water", width: 2, height: 2) {
			state "dry", icon:"st.alarm.water.dry", backgroundColor:"#ffffff"
			state "wet", icon:"st.alarm.water.wet", backgroundColor:"#53a7c0"
		}
		standardTile("temperature", "device.temperature", width: 2, height: 2) {
			state "normal", icon:"st.alarm.temperature.normal", backgroundColor:"#ffffff"
			state "freezing", icon:"st.alarm.temperature.freeze", backgroundColor:"#53a7c0"
			state "overheated", icon:"st.alarm.temperature.overheat", backgroundColor:"#F80000"
		}
		valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false) {
			state "battery", label:'${currentValue}% battery', unit:""/*, backgroundColors:[
				[value: 5, color: "#BC2323"],
				[value: 10, color: "#D04E00"],
				[value: 15, color: "#F1D801"],
				[value: 16, color: "#FFFFFF"]
			]*/
		}
		main (["water", "temperature"])
		details(["water", "temperature", "battery"])
	}
}

def parse(String description) {
	def result = []
	def parsedZwEvent = zwave.parse(description, [0x30: 1, 0x71: 2, 0x84: 1])

	if(parsedZwEvent) {
		if(parsedZwEvent.CMD == "8407") {
			def lastStatus = device.currentState("battery")
			def ageInMinutes = lastStatus ? (new Date().time - lastStatus.date.time)/60000 : 600
			log.debug "Battery status was last checked ${ageInMinutes} minutes ago"

			if (ageInMinutes >= 600) {
				log.debug "Battery status is outdated, requesting battery report"
				result << new physicalgraph.device.HubAction(zwave.batteryV1.batteryGet().format())
			}
			result << new physicalgraph.device.HubAction(zwave.wakeUpV1.wakeUpNoMoreInformation().format())
		}
		result << createEvent( zwaveEvent(parsedZwEvent) )
	}
	if(!result) result = [ descriptionText: parsedZwEvent, displayed: false ]
	log.debug "Parse returned ${result}"
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd)
{
	[descriptionText: "${device.displayName} woke up", isStateChange: false]
}

def zwaveEvent(physicalgraph.zwave.commands.sensorbinaryv1.SensorBinaryReport cmd)
{
	def map = [:]
	map.name = "water"
	map.value = cmd.sensorValue ? "wet" : "dry"
	map.descriptionText = "${device.displayName} is ${map.value}"
	map
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	def map = [:]
	if(cmd.batteryLevel == 0xFF) {
		map.name = "battery"
		map.value = 1
		map.descriptionText = "${device.displayName} has a low battery"
		map.displayed = true
	} else {
		map.name = "battery"
		map.value = cmd.batteryLevel > 0 ? cmd.batteryLevel.toString() : 1
		map.unit = "%"
		map.displayed = false
	}
	map
}

def zwaveEvent(physicalgraph.zwave.commands.alarmv2.AlarmReport cmd)
{
	def map = [:]
	if (cmd.zwaveAlarmType == physicalgraph.zwave.commands.alarmv2.AlarmReport.ZWAVE_ALARM_TYPE_WATER) {
		map.name = "water"
		map.value = cmd.alarmLevel ? "wet" : "dry"
		map.descriptionText = "${device.displayName} is ${map.value}"
	}
	if(cmd.zwaveAlarmType ==  physicalgraph.zwave.commands.alarmv2.AlarmReport.ZWAVE_ALARM_TYPE_HEAT) {
		map.name = "temperature"
		if(cmd.zwaveAlarmEvent == 1) { map.value = "overheated"}
		if(cmd.zwaveAlarmEvent == 2) { map.value = "overheated"}
		if(cmd.zwaveAlarmEvent == 3) { map.value = "changing temperature rapidly"}
		if(cmd.zwaveAlarmEvent == 4) { map.value = "changing temperature rapidly"}
		if(cmd.zwaveAlarmEvent == 5) { map.value = "freezing"}
		if(cmd.zwaveAlarmEvent == 6) { map.value = "freezing"}
		if(cmd.zwaveAlarmEvent == 254) { map.value = "normal"}
		map.descriptionText = "${device.displayName} is ${map.value}"
	}

	map
}

def zwaveEvent(physicalgraph.zwave.Command cmd)
{
	log.debug "COMMAND CLASS: $cmd"
}

