/**
 *  Presence Change Text
 *
 *  Author: SmartThings
 */
definition(
    name: "Presence Change Text",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Send me a text message when my presence status changes.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/text_presence.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/text_presence@2x.png"
)

preferences {
	section("When a presence sensor arrives or departs this location..") {
		input "presence", "capability.presenceSensor", title: "Which sensor?"
	}
	section("Send a text message to...") {
        input("recipients", "contact", title: "Send notifications to") {
            input "phone1", "phone", title: "Phone number?"
        }
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

        if (location.contactBookEnabled) {
            sendNotificationToContacts("${presence.label ?: presence.name} has arrived at the ${location}", recipients)
        }
        else {
            sendSms(phone1, "${presence.label ?: presence.name} has arrived at the ${location}")
        }
	} else if (evt.value == "not present") {
		log.debug "${presence.label ?: presence.name} has left the ${location}"

        if (location.contactBookEnabled) {
            sendNotificationToContacts("${presence.label ?: presence.name} has left the ${location}", recipients)
        }
        else {
            sendSms(phone1, "${presence.label ?: presence.name} has left the ${location}")
        }
	}
}
