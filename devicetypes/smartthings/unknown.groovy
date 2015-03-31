metadata {
	// Automatically generated. Make future change here.
	definition (name: "Unknown", namespace: "smartthings", author: "SmartThings") {
	}

	// simulator metadata
	simulator {
		// Not Applicable to Unknown Device
	}

	// UI tile definitions
	tiles {
		standardTile("unknown", "device.unknown", width: 2, height: 2) {
			state(name:"default", icon:"st.unknown.unknown.unknown", backgroundColor:"#767676", label: "Unknown")
		}

		main "unknown"
		details "unknown"
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	// None
}
