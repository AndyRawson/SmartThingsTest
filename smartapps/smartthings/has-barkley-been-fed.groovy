/**
 *  Has Barkley Been Fed
 *
 *  Author: SmartThings
 */
definition(
    name: "Has Barkley Been Fed?",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Setup a schedule to be reminded to feed your pet. Purchase any SmartThings certified pet food feeder and install the Feed My Pet app, and set the time. You and your pet are ready to go. Your life just got smarter.",
    category: "Pets",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/dogfood_feeder.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/dogfood_feeder@2x.png"
)

preferences {
	section("Choose your pet feeder...") {
		input "feeder1", "capability.contactSensor", title: "Where?"
	}
	section("Feed my pet at...") {
		input "time1", "time", title: "When?"
	}
	section("Text me if I forget...") {
        input("recipients", "contact", title: "Send notifications to") {
            input "phone1", "phone", title: "Phone number?"
        }
	}
}

def installed()
{
	schedule(time1, "scheduleCheck")
}

def updated()
{
	unsubscribe() //TODO no longer subscribe like we used to - clean this up after all apps updated
	unschedule()
	schedule(time1, "scheduleCheck")
}

def scheduleCheck()
{
	log.trace "scheduledCheck"

	def midnight = (new Date()).clearTime()
	def now = new Date()
	def feederEvents = feeder1.eventsBetween(midnight, now)
	log.trace "Found ${feederEvents?.size() ?: 0} feeder events since $midnight"
	def feederOpened = feederEvents.count { it.value && it.value == "open" } > 0

	if (feederOpened) {
		log.debug "Feeder was opened since $midnight, no SMS required"
	} else {
        if (location.contactBookEnabled) {
            log.debug "Feeder was not opened since $midnight, texting contacts:${recipients?.size()}"
            sendNotificationToContacts("No one has fed the dog", recipients)
        }
        else {
            log.debug "Feeder was not opened since $midnight, texting $phone1"
            sendSms(phone1, "No one has fed the dog")
        }
	}
}
