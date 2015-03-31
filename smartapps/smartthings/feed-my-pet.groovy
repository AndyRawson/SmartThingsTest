/**
 *  Feed My Pet
 *
 *  Author: SmartThings
 */
definition(
    name: "Feed My Pet",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Setup a schedule for when your pet is fed. Purchase any SmartThings certified pet food feeder and install the Feed My Pet app, and set the time. You and your pet are ready to go. Your life just got smarter.",
    category: "Pets",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/dogfood_feeder.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/dogfood_feeder@2x.png"
)

preferences {
	section("Choose your pet feeder...") {
		input "feeder", "device.PetFeederShield", title: "Where?"
	}
	section("Feed my pet at...") {
		input "time1", "time", title: "When?"
	}
}

def installed()
{
	schedule(time1, "scheduleCheck")
}

def updated()
{
	unschedule()
	schedule(time1, "scheduleCheck")
}

def scheduleCheck()
{
	log.trace "scheduledFeeding"
	feeder?.feed()
}
