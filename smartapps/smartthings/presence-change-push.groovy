/**
 *  Presence Change Push
 *
 *  Author: SmartThings
 */
definition(
    name: "Presence Change Push",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Get a push notification when a SmartSense Presence tag or smartphone arrives at or departs from a location.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/text_presence.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/text_presence@2x.png"
)

preferences {
	section("When a presence sensor arrives or departs this location..") {
		input "presence", "capability.presenceSensor", title: "Which sensor?"
	}
}

def installed() {
	subscribe(presence, "presence", presenceHandler)
}

def updated() {
	unsubscribe()
	subscribe(presence, "presence", presenceHandler)
}

def presenceHandler(evt) {
	if (evt.value == "present") {
		log.debug "${presence.label ?: presence.name} has arrived at the ${location}"
    	sendPush("${presence.label ?: presence.name} has arrived at the ${location}")
	} else if (evt.value == "not present") {
		log.debug "${presence.label ?: presence.name} has left the ${location}"
    	sendPush("${presence.label ?: presence.name} has left the ${location}")
	}
}
