metadata {
	// Automatically generated. Make future change here.
	definition (name: "Simulated Smoke Alarm", namespace: "smartthings/testing", author: "SmartThings") {
		capability "Smoke Detector"
		capability "Sensor"

        command "smoke"
        command "test"
        command "clear"
	}

	simulator {

	}

	tiles {
		standardTile("main", "device.smoke", width: 2, height: 2) {
			state("clear", label:"Clear", icon:"st.alarm.smoke.clear", backgroundColor:"#ffffff", action:"smoke")
			state("detected", label:"Smoke!", icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13", action:"clear")
			state("tested", label:"Test", icon:"st.alarm.smoke.test", backgroundColor:"#e86d13", action:"clear")
		}
 		standardTile("smoke", "device.smoke", inactiveLabel: false, decoration: "flat") {
			state "default", label:'Smoke', action:"smoke"
		}  
 		standardTile("test", "device.smoke", inactiveLabel: false, decoration: "flat") {
			state "default", label:'Test', action:"test"
		}
 		standardTile("reset", "device.smoke", inactiveLabel: false, decoration: "flat") {
			state "default", label:'Clear', action:"clear"
		}
        main "main"
		details(["main", "smoke", "test", "clear"])
	}
}

def parse(String description) {
	
}

def smoke() {
	log.debug "smoke()"
	sendEvent(name: "smoke", value: "detected", descriptionText: "$device.displayName smoke detected!")
}

def test() {
	log.debug "test()"
	sendEvent(name: "smoke", value: "tested", descriptionText: "$device.displayName tested")
}

def clear() {
	log.debug "clear()"
	sendEvent(name: "smoke", value: "clear", descriptionText: "$device.displayName clear")
}
