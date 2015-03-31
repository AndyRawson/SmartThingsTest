/**
 *  Darken Behind Me
 *
 *  Author: SmartThings
 */
definition(
    name: "Darken Behind Me",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Turn your lights off after a period of no motion being observed.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet@2x.png"
)

preferences {
	section("When there's no movement...") {
		input "motion1", "capability.motionSensor", title: "Where?"
	}
	section("Turn off a light...") {
		input "switch1", "capability.switch", multiple: true
	}
}

def installed()
{
	subscribe(motion1, "motion.inactive", motionInactiveHandler)
}

def updated()
{
	unsubscribe()
	subscribe(motion1, "motion.inactive", motionInactiveHandler)
}

def motionInactiveHandler(evt) {
	switch1.off()
}
