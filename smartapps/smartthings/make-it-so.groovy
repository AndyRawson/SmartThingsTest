/**
 *  Make it So
 *
 *  Author: SmartThings
 *  Date: 2013-03-06
 */
definition(
    name: "Make It So",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Saves the states of a specified set switches and thermostat setpoints and restores them at each mode change. To use 1) Set the mode, 2) Change switches and setpoint to where you want them for that mode, and 3) Install or update the app. Changing to that mode or touching the app will set the devices to the saved state.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_thermo-switch.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_thermo-switch@2x.png"
)

preferences {
	section("Switches") {
		input "switches", "capability.switch", multiple: true, required: false
	}
	section("Thermostats") {
		input "thermostats", "capability.thermostat", multiple: true, required: false
	}
	section("Locks") {
		input "locks", "capability.lock", multiple: true, required: false
	}
}

def installed() {
	subscribe(location, changedLocationMode)
	subscribe(app, appTouch)
	saveState()
}

def updated() {
	unsubscribe()
	subscribe(location, changedLocationMode)
	subscribe(app, appTouch)
	saveState()
}

def appTouch(evt)
{
	restoreState(currentMode)
}

def changedLocationMode(evt)
{
	restoreState(evt.value)
}

private restoreState(mode)
{
	log.info "restoring state for mode '$mode'"
	def map = state[mode] ?: [:]
	switches?.each {
		def value = map[it.id]
		if (value?.switch == "on") {
			def level = value.level
			if (level) {
				log.debug "setting $it.label level to $level"
				it.setLevel(level)
			}
			else {
				log.debug "turning $it.label on"
				it.on()
			}
		}
		else if (value?.switch == "off") {
			log.debug "turning $it.label off"
			it.off()
		}
	}

	thermostats?.each {
		def value = map[it.id]
		if (value?.coolingSetpoint) {
			log.debug "coolingSetpoint = $value.coolingSetpoint"
			it.setCoolingSetpoint(value.coolingSetpoint)
		}
		if (value?.heatingSetpoint) {
			log.debug "heatingSetpoint = $value.heatingSetpoint"
			it.setHeatingSetpoint(value.heatingSetpoint)
		}
	}

	locks?.each {
		def value = map[it.id]
		if (value) {
			if (value?.locked) {
				it.lock()
			}
			else {
				it.unlock()
			}
		}
	}
}


private saveState()
{
	def mode = currentMode
	def map = state[mode] ?: [:]

	switches?.each {
		map[it.id] = [switch: it.currentSwitch, level: it.currentLevel]
	}

	thermostats?.each {
		map[it.id] = [coolingSetpoint: it.currentCoolingSetpoint, heatingSetpoint: it.currentHeatingSetpoint]
	}

	locks?.each {
		map[it.id] = [locked: it.currentLock == "locked"]
	}

	state[mode] = map
	log.debug "saved state for mode ${mode}: ${state[mode]}"
	log.debug "state: $state"
}

private getCurrentMode()
{
	location.mode ?: "_none_"
}
