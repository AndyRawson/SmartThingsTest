metadata {
	// Automatically generated. Make future change here.
	definition (name: "Particulate Detector", namespace: "smartthings", author: "SmartThings") {
	}

	// simulator metadata
	simulator {
		// TBD
	}

	// tile definitions
	tiles {
		standardTile("particulate", "device.particulate", width: 2, height: 2) {
			state "default", icon: "st.particulate.particulate.particulate", backgroundColor: "#ffffff"
		}

		main "particulate"
		details "particulate"
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	// TBD
}
