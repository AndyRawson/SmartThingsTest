/**
 *  Nest Thermostat (device type)
 *
 *  Author: Juan Pablo Risso (juan@smartthings.com)
 *
 *  Date: 2015-02-05
 *
 */

 // for the UI
metadata {
    definition (name: "Nest Thermostat", namespace: "smartthings", author: "juano23@gmail.com") {
        capability "Relative Humidity Measurement"
        capability "Thermostat"
        capability "Polling"        
        capability "Temperature Measurement"

        attribute "leafinfo", "string"
        attribute "presence", "string"
        attribute "emergencyheat", "string"
        attribute "canheat", "string"
        attribute "cancool", "string"
        
        command "mode"
        command "coolup"
        command "cooldown"
        command "heatup"
        command "heatdown"      
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles {
        valueTile("temperature", "device.temperature", width: 1, height: 1, canChangeIcon: true) {
            state("temperature", label: '${currentValue}°', unit:"F", backgroundColors: [
            		[value: '', color: "#ffffff"],
                    [value: 31, color: "#153591"],
                    [value: 44, color: "#1e9cbb"],
                    [value: 59, color: "#90d2a7"],
                    [value: 74, color: "#44b621"],
                    [value: 84, color: "#f1d801"],
                    [value: 95, color: "#d04e00"],
                    [value: 96, color: "#bc2323"]
                ]
            )
        }
        standardTile("lowarrowup", "device.lowarrowup", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"heatup", backgroundColor:"#ffffff", icon:"st.thermostat.thermostat-up"
        }
        standardTile("higharrowup", "device.higharrowup", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"coolup", backgroundColor:"#ffffff", icon:"st.thermostat.thermostat-up"
        }        
		standardTile("thermostatMode", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
            state("waiting", label:'${name}', icon: "st.unknown.unknown.unknown")
			state("heat-cool", label:'${name}', action:"mode", icon: "st.tesla.tesla-hvac")
            state("off", action:"mode", icon: "st.thermostat.heating-cooling-off")
            state("cool", action:"mode", icon: "st.thermostat.cool")
            state("heat", action:"mode", icon: "st.thermostat.heat")
            state("offline", label:'${name}', icon: "st.illuminance.illuminance.dark")
            state("away", label:'${name}', icon: "st.nest.nest-away")
            state("emergency", icon: "st.thermostat.emergency-heat")
            state("auto-away", label:'${name}', icon: "st.nest.nest-away")
			state("rushhour", label:'rush hour', icon: "st.Home.home1")            
        }
        valueTile("heatingSetpoint", "device.heatingSetpoint", inactiveLabel: false) {
            state "default", label:'${currentValue}°', unit:"F", backgroundColor:"#ffffff", icon:"st.appliances.appliances8"
        }
        valueTile("coolingSetpoint", "device.coolingSetpoint", inactiveLabel: false) {
            state "default", label:'${currentValue}°', unit:"F", backgroundColor:"#ffffff", icon:"st.appliances.appliances8"
        }        
        valueTile("humidity", "device.humidity", inactiveLabel: false) {
            state "default", label:'${currentValue}% Humidity', unit:"Humidity"
        }
        standardTile("lowarrowdown", "device.lowarrowdown", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"heatdown", backgroundColor:"#ffffff", icon:"st.thermostat.thermostat-down"
        }
        standardTile("higharrowdown", "device.higharrowdown", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"cooldown", backgroundColor:"#ffffff", icon:"st.thermostat.thermostat-down"
        } 
		standardTile("leafinfo", "device.leafinfo", inactiveLabel: false, decoration: "flat") {
            state "yes", label:'', icon: "st.nest.nest-leaf"
            state "no", label:'', icon: "st.illuminance.illuminance.dark"
        }
        standardTile("presence", "device.presence", inactiveLabel: false, decoration: "flat") {
            state "home", label:'${name}', action:"away", icon: "st.nest.nest-home"
            state "away", label:'${name}', action:"present", icon: "st.nest.nest-away"
            state "auto-away", label:'${name}', action:"present", icon: "st.nest.nest-away"
        }
		standardTile("emergencyheat", "device.emergencyheat", inactiveLabel: false, decoration: "flat") {
            state "yes", label:'', icon: "st.thermostat.emergency-heat"
            state "no", label:'', icon: "st.illuminance.illuminance.dark"
        }        
		standardTile("thermostatFanMode", "device.thermostatFanMode", inactiveLabel: false, decoration: "flat") {
            state "auto", label:'${name}', action:"thermostat.fanOn", icon: "st.Appliances.appliances11"
            state "on", label:'${name}', action:"thermostat.fanCirculate", icon: "st.Appliances.appliances11"
            state "circulate", label:'${name}', action:"thermostat.fanAuto", icon: "st.Appliances.appliances11"
        }
		standardTile("refresh", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
            state "default", action:"polling.poll", icon:"st.secondary.refresh"
        }
        main "temperature"
    	details(["temperature", "lowarrowup", "higharrowup", "thermostatMode", "heatingSetpoint", "coolingSetpoint", "humidity", "lowarrowdown", "higharrowdown", "presence", "leafinfo", "emergencyheat", "refresh"])
    }
}

// handle commands

def away() {
    setPresence('away')
}

def present() {
    setPresence('present')
}

def setPresence(status) {
    log.debug "Presence: $status"
    parent.presence(status)
}

def poll() {
    log.debug "Executing 'poll'"
    parent.poll()
}

def heatup() {
    def heatingvalue = device.latestState('heatingSetpoint').value
    log.trace "Heat up from $heatingvalue"
    if (heatingvalue) {
        def targetvalue = heatingvalue as BigDecimal
        def scale= getTemperatureScale().toLowerCase()   
        if (scale == "f")
            targetvalue = targetvalue + 1 
        else  
            targetvalue = targetvalue + 0.5 
        setHeatingSetpoint(targetvalue) 
    } else {
    	def latestThermostatMode = device.latestState('thermostatMode').stringValue 
        parent.sendNotification("This action is not available in mode $latestThermostatMode")
    }     
}

def heatdown() {
    def heatingvalue = device.latestState('heatingSetpoint').value
    log.trace "Heat down from $heatingvalue"
    if (heatingvalue) {
        def targetvalue = heatingvalue as BigDecimal
        def scale= getTemperatureScale().toLowerCase()   
        if (scale == "f")
            targetvalue = targetvalue - 1 
        else  
            targetvalue = targetvalue - 0.5 
        setHeatingSetpoint(targetvalue) 
    } else {
    	def latestThermostatMode = device.latestState('thermostatMode').stringValue 
        parent.sendNotification("This action is not available in mode $latestThermostatMode")
    }        
}

def coolup() {
    def coolingvalue = device.latestState('coolingSetpoint').value 
    log.trace "Cool up from $coolingvalue"
    if (coolingvalue) {
        def targetvalue = coolingvalue as BigDecimal 
        def scale= getTemperatureScale().toLowerCase()   
        if (scale == "f")
            targetvalue = targetvalue + 1 
        else
            targetvalue = targetvalue + 0.5  
        setCoolingSetpoint(targetvalue)  
    } else {
    	def latestThermostatMode = device.latestState('thermostatMode').stringValue 
        parent.sendNotification("This action is not available in mode $latestThermostatMode")
    }
}

def cooldown() {
    def coolingvalue = device.latestState('coolingSetpoint').value 
    log.trace "Cool down from $coolingvalue"
    if (coolingvalue) {
        def targetvalue = coolingvalue as BigDecimal 
        def scale= getTemperatureScale().toLowerCase()   
        if (scale == "f")
            targetvalue = targetvalue - 1 
        else
            targetvalue = targetvalue - 0.5  
        setCoolingSetpoint(targetvalue)  
    } else {
    	def latestThermostatMode = device.latestState('thermostatMode').stringValue 
        parent.sendNotification("This action is not available in mode $latestThermostatMode")
    }
}

def mode() {
    log.trace "Switch Mode"
    parent.poll()
    def canheatvalue = device.latestState('canheat').stringValue
    def cancoolvalue = device.latestState('cancool').stringValue 
    def latestThermostatMode = device.latestState('thermostatMode').stringValue
    if (latestThermostatMode == "off" && canheatvalue == "yes")
    	parent.mode(device.deviceNetworkId,"heat")
    else if ((latestThermostatMode == "heat" && cancoolvalue == "yes") || (latestThermostatMode == "off" && canheatvalue == "no"))
    	parent.mode(device.deviceNetworkId,"cool")  
    else if (latestThermostatMode == "cool" && canheatvalue == "yes")
    	parent.mode(device.deviceNetworkId,"heat-cool")  
    else if (latestThermostatMode == "heat-cool" || (latestThermostatMode == "heat" && cancoolvalue == "no") || (latestThermostatMode == "cool" && canheatvalue == "no"))
    	parent.mode(device.deviceNetworkId,"off")  
}

void setHeatingSetpoint(temp) {
    parent.poll()
    log.trace "setHeatingSetpoint to $temp"
    def min
    def max    
    def targetvalue = temp 
    def scale= getTemperatureScale().toLowerCase()   
    if (scale == "f") {
        min = 50
        max = 90        
    } else {  
        min = 9
        max = 32        
    }         
   	if(targetvalue >= min && targetvalue <= max) {     
        def latestThermostatMode = device.latestState('thermostatMode').stringValue     
        switch (latestThermostatMode) {
            case "heat-cool":
                parent.temp(device.deviceNetworkId, "target_temperature_low_${scale}", targetvalue)
                break;
            case "heat":
                parent.temp(device.deviceNetworkId, "target_temperature_${scale}", targetvalue)
                break;        
        }  
	} else {
    	parent.sendNotification("The value is out of the allowed range")
    }     
}

void setCoolingSetpoint(temp) {
    parent.poll()
    log.trace "setCoolingSetpoint to $temp"
    def min
    def max    
    def targetvalue = temp 
    def scale= getTemperatureScale().toLowerCase()   
    if (scale == "f") {
        min = 50
        max = 90        
    } else {  
        min = 9
        max = 32        
    }         
   	if(targetvalue >= min && targetvalue <= max) {     
        def latestThermostatMode = device.latestState('thermostatMode').stringValue     
        switch (latestThermostatMode) {
            case "heat-cool":
                parent.temp(device.deviceNetworkId, "target_temperature_high_${scale}", targetvalue)
                break;
            case "cool":
                parent.temp(device.deviceNetworkId, "target_temperature_${scale}", targetvalue)
                break;        
        }  
	} else {
    	parent.sendNotification("The value is out of the allowed range")
    }     
}