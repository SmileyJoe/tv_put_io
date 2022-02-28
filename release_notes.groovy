import groovy.json.JsonSlurper

ext.generateReleaseNotes = {flavour ->
    def json = new JsonSlurper().parseText(new File('release_notes.json').text)
    def (version, feature, bug) = json.get("previous_release_" + flavour).tokenize('.')
    bug++
    def notes = json.notes
    def release_notes = ""

    while(notes.keySet().contains(version)){
        def notes_version = notes.get(version)
        while(notes_version.keySet().contains(feature)){
            def notes_feature = notes_version.get(feature)
            while(notes_feature.keySet().contains(bug)){
                notes_feature.get(bug).each{note -> release_notes += " - " + note + "\n"}
                bug++
            }
            bug = "0"
            feature++
        }
        feature = "0"
        version++
    }

    return release_notes
}