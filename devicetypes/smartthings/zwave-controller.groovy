metadata {
	// Automatically generated. Make future change here.
	definition (name: "Z-Wave Controller", namespace: "smartthings", author: "SmartThings") {

		command "on"
		command "off"

		fingerprint deviceId: "0x02"
	}

	simulator {

	}

	tiles {
		standardTile("state", "device.state", width: 2, height: 2) {
			state 'connected', icon: "st.unknown.zwave.static-controller", backgroundColor:"#ffffff"
		}
		standardTile("basicOn", "device.switch", inactiveLabel:false, decoration:"flat") {
			state "on", label:"on", action:"on", icon:"st.switches.switch.on"
		}
		standardTile("basicOff", "device.switch", inactiveLabel: false, decoration:"flat") {
			state "off", label:"off", action:"off", icon:"st.switches.switch.off"
		}

		main "state"
		details(["state", "basicOn", "basicOff"])
	}
}

def parse(String description) {
	def result = null
	if (description.startsWith("Err")) {
	    result = createEvent(descriptionText:description, displayed:true)
	} else {
		def cmd = zwave.parse(description)
		if (cmd) {
			result = createEvent(zwaveEvent(cmd))
		}
	}
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	def event = [isStateChange: true]
	event.linkText = device.label ?: device.name
	event.descriptionText = "$event.linkText: ${cmd.encapsulatedCommand()} [secure]"
	event
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	def event = [isStateChange: true]
	event.linkText = device.label ?: device.name
	event.descriptionText = "$event.linkText: $cmd"
	event
}

def on() {
	zwave.basicV1.basicSet(value: 0xFF).format()
}

def off() {
	zwave.basicV1.basicSet(value: 0x00).format()
}
