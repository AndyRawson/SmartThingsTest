/**
 *  SmartAlert Siren
 *
 *  Author: SmartThings
 *  Date: 2013-03-05
 */
metadata {
	// Automatically generated. Make future change here.
	definition (name: "SmartAlert Siren", namespace: "smartthings", author: "SmartThings") {
		capability "Actuator"
		capability "Switch"
		capability "Sensor"
		capability "Alarm"

		command "test"

		fingerprint deviceId: "0x1100", inClusters: "0x26,0x71"
	}

	simulator {
		// reply messages
		reply "2001FF,2002": "command: 2003, payload: FF"
		reply "200100,2002": "command: 2003, payload: 00"
		reply "200121,2002": "command: 2003, payload: 21"
		reply "200142,2002": "command: 2003, payload: 42"
		reply "2001FF,delay 3000,200100,2002": "command: 2003, payload: 00"
	}

	tiles {
		standardTile("alarm", "device.alarm", width: 2, height: 2) {
			state "off", label:'off', action:'alarm.strobe', icon:"st.alarm.alarm.alarm", backgroundColor:"#ffffff"
			state "strobe", label:'strobe!', action:'alarm.off', icon:"st.alarm.alarm.alarm", backgroundColor:"#e86d13"
			state "siren", label:'siren!', action:'alarm.off', icon:"st.alarm.alarm.alarm", backgroundColor:"#e86d13"
			state "both", label:'alarm!', action:'alarm.off', icon:"st.alarm.alarm.alarm", backgroundColor:"#e86d13"
		}
		standardTile("strobe", "device.alarm", inactiveLabel: false, decoration: "flat") {
			state "off", label:'', action:"alarm.strobe", icon:"st.secondary.strobe", backgroundColor:"#cccccc"
			state "siren", label:'', action:"alarm.strobe", icon:"st.secondary.strobe", backgroundColor:"#cccccc"
			state "strobe", label:'', action:'alarm.strobe', icon:"st.secondary.strobe", backgroundColor:"#e86d13"
			state "both", label:'', action:'alarm.strobe', icon:"st.secondary.strobe", backgroundColor:"#e86d13"
		}
		standardTile("siren", "device.alarm", inactiveLabel: false, decoration: "flat") {
			state "off", label:'', action:"alarm.siren", icon:"st.secondary.siren", backgroundColor:"#cccccc"
			state "strobe", label:'', action:"alarm.siren", icon:"st.secondary.siren", backgroundColor:"#cccccc"
			state "siren", label:'', action:'alarm.siren', icon:"st.secondary.siren", backgroundColor:"#e86d13"
			state "both", label:'', action:'alarm.siren', icon:"st.secondary.siren", backgroundColor:"#e86d13"
		}
		standardTile("test", "device.alarm", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"test", icon:"st.secondary.test"
		}
		standardTile("off", "device.alarm", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"alarm.off", icon:"st.secondary.off"
		}
		main "alarm"
		details(["alarm","strobe","siren","test","off"])
	}
}

def on() {
	[
		zwave.basicV1.basicSet(value: 0xFF).format(),
		zwave.basicV1.basicGet().format()
	]
}

def off() {
	[
		zwave.basicV1.basicSet(value: 0x00).format(),
		zwave.basicV1.basicGet().format()
	]
}

def test() {
	[
		zwave.basicV1.basicSet(value: 0xFF).format(),
		"delay 3000",
		zwave.basicV1.basicSet(value: 0x00).format(),
		zwave.basicV1.basicGet().format()
	]
}

def strobe() {
	[
		zwave.basicV1.basicSet(value: 0x21).format(),
		zwave.basicV1.basicGet().format()
	]
}

def siren() {
	[
		zwave.basicV1.basicSet(value: 0x42).format(),
		zwave.basicV1.basicGet().format()
	]
}

def both() {
	[
		zwave.basicV1.basicSet(value: 0xFF).format(),
		zwave.basicV1.basicGet().format()
	]
}


def parse(String description) {
	log.debug "parse($description)"
	def result = null
	def cmd = zwave.parse(description, [0x20: 1])
	if (cmd) {
		result = createEvents(cmd)
	}
	log.debug "Parse returned ${result?.descriptionText}"
	return result
}

def createEvents(physicalgraph.zwave.commands.basicv1.BasicReport cmd)
{
	def switchValue = cmd.value ? "on" : "off"
	def alarmValue
	if (cmd.value == 0) {
		alarmValue = "off"
	}
	else if (cmd.value <= 33) {
		alarmValue = "strobe"
	}
	else if (cmd.value <= 66) {
		alarmValue = "siren"
	}
	else {
		alarmValue = "both"
	}
	[
		createEvent([name: "switch", value: switchValue, type: "digital", displayed: false]),
		createEvent([name: "alarm", value: alarmValue, type: "digital"])
	]
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.warn "UNEXPECTED COMMAND: $cmd"
}
