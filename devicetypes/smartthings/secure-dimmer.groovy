metadata {
	// Automatically generated. Make future change here.
	definition (name: "Secure Dimmer", namespace: "smartthings", author: "SmartThings") {
		capability "Switch Level"
		capability "Actuator"
		capability "Switch"
		capability "Refresh"
		capability "Sensor"

		attribute "secure", "string"

		fingerprint deviceId: "0x1101", inClusters: "0x98,0x86,0x72"
	}

	simulator {
		status "on":  "command: 9881, payload: 002603FF"
		status "off": "command: 9881, payload: 00260300"
		status "09%": "command: 9881, payload: 00260309"
		status "10%": "command: 9881, payload: 0026030A"
		status "33%": "command: 9881, payload: 00260321"
		status "66%": "command: 9881, payload: 00260342"
		status "99%": "command: 9881, payload: 00260363"

		// reply messages
		reply "9881002001FF,delay 100,9881002602": "command: 9881, payload: 002603FF"
		reply "988100200100,delay 100,9881002602": "command: 9881, payload: 00260300"
		reply "988100200119,delay 100,9881002602": "command: 9881, payload: 00260319"
		reply "988100200132,delay 100,9881002602": "command: 9881, payload: 00260332"
		reply "98810020014B,delay 100,9881002602": "command: 9881, payload: 0026034B"
		reply "988100200163,delay 100,9881002602": "command: 9881, payload: 00260363"
	}

	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
			state "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
			state "turningOn", label:'${name}', icon:"st.switches.switch.on", backgroundColor:"#79b821"
			state "turningOff", label:'${name}', icon:"st.switches.switch.off", backgroundColor:"#ffffff"
		}
		controlTile("levelSliderControl", "device.level", "slider", height: 2, width: 1, inactiveLabel: false) {
			state "level", action:"switch level.setLevel"
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		main(["switch"])
		details(["switch", "levelSliderControl", "refresh"])
	}
}

def parse(String description) {
	def cmd = zwave.parse(description, [0x20: 1, 0x26: 3, 0x70: 1, 0x32:3])
	if (cmd) {
		zwaveEvent(cmd)
	} else {
		log.debug("Couldn't zwave.parse '$description'")
		null
	}
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	def encapsulatedCommand = cmd.encapsulatedCommand([0x20: 1, 0x26: 3, 0x32: 3])
	zwaveEvent = zwaveEvent(encapsulatedCommand)

	if (device.currentValue("secure") != "yes") {
		setSecure = [name: "secure", value: "yes", descriptionText: "$device.name is secure", displayed: false]
		[zwaveEvent, setSecure]
	} else {
		zwaveEvent
	}
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	dimmerEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv1.SwitchMultilevelReport cmd) {
	dimmerEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelReport cmd) {
	dimmerEvents(cmd)
}

def dimmerEvents(physicalgraph.zwave.Command cmd) {
	def switchEvent = [name: "switch"]
	switchEvent.linkText = getLinkText(device)
	switchEvent.value = cmd.value ? "on" : "off"
	switchEvent.handlerName = cmd.value ? "statusOn" : "statusOff"
	switchEvent.descriptionText = "${switchEvent.linkText} is ${switchEvent.value}"
	switchEvent.canBeCurrentState = true
	switchEvent.isStateChange = isStateChange("switch", switchEvent.value)

	def levelEvent = [name: "level"]
	levelEvent.linkText = switchEvent.linkText
	levelEvent.value = cmd.value as String
	levelEvent.unit = "%"
	levelEvent.descriptionText = "${switchEvent.linkText} set to ${levelEvent.value}%"
	levelEvent.canBeCurrentState = true
	levelEvent.isStateChange = isStateChange("level", levelEvent.value)

	if (cmd.value == 0 || cmd.value >= 99) {
		// All the way off or on, don't show level event
		switchEvent.displayed = switchEvent.isStateChange
		levelEvent.displayed = !switchEvent.displayed
	} else {
		// Turned on to a specific percentage, show level event
		switchEvent.displayed = false
		levelEvent.displayed = levelEvent.isStateChange
	}
	[switchEvent, levelEvent]
}

def zwaveEvent(physicalgraph.zwave.commands.meterv3.MeterReport cmd) {
	if (cmd.meterType == 1) {
		if (cmd.scale == 0) {
			createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kWh")
		} else if (cmd.scale == 1) {
			createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kVAh")
		} else if (cmd.scale == 2) {
			createEvent(name: "power", value: cmd.scaledMeterValue, unit: "W")
		} else {
			createEvent(name: "electric", value: cmd.scaledMeterValue, unit: ["pulses", "V", "A", "R/Z", ""][cmd.scale - 3])
		}
	} else if (cmd.meterType == 2) {
		createEvent(name: "gas", value: cmd.scaledMeterValue, unit: ["m^3", "ft^3", "", "pulses", ""][cmd.scale])
	} else if (cmd.meterType == 3) {
		createEvent(name: "water", value: cmd.scaledMeterValue, unit: ["m^3", "ft^3", "gal"][cmd.scale])
	} else {
		null
	}
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	def linkText = device.label ?: device.name
	[linkText: linkText, descriptionText: "$linkText: $cmd", displayed: false]
}

def on() {
	secureSequence([
		zwave.basicV1.basicSet(value: 0xFF),
		zwave.switchMultilevelV1.switchMultilevelGet()
	])
}

def off() {
	secureSequence([
		zwave.basicV1.basicSet(value: 0x00),
		zwave.switchMultilevelV1.switchMultilevelGet()
	])
}

def setLevel(value) {
	secureSequence([
		zwave.basicV1.basicSet(value: value),
		zwave.switchMultilevelV1.switchMultilevelGet()
	])
}

def setLevel(value, duration) {
	def dimmingDuration = duration < 128 ? duration : 128 + Math.round(duration / 60)
	secure(zwave.switchMultilevelV2.switchMultilevelSet(value: value, dimmingDuration: dimmingDuration))
}

def refresh() {
	secure(zwave.switchMultilevelV1.switchMultilevelGet())
}

def secure(physicalgraph.zwave.Command cmd) {
	if (device.currentValue("secure") == "yes") {
		zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
	} else {
		cmd.format()
	}
}

def secureSequence(Collection commands, ...delayBetweenArgs) {
	delayBetween(commands.collect{ secure(it) }, *delayBetweenArgs)
}
