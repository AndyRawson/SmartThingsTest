//Thing is used while a device is in the process of being joined

metadata {
	// Automatically generated. Make future change here.
	definition (name: "Thing", namespace: "smartthings", author: "SmartThings") {
	}

	// simulator metadata
	simulator {
		// Not Applicable to Thing Device
	}

	// UI tile definitions
	tiles {
		standardTile("thing", "device.thing", width: 2, height: 2) {
			state(name:"default", icon: "st.unknown.thing.thing-circle", label: "Please Wait")
		}

		main "thing"
		details "thing"
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	// None
}
