import groovy.json.JsonSlurper

ext.generateReleaseNotes = {flavour ->
    def notesFile = new File(projectDir.getParentFile(), 'release_notes.json')
    def json = new JsonSlurper().parseText(notesFile.text)
    def version, feature, bug
    def type

    if(flavour.equalsIgnoreCase("store")){
        (version, feature, bug) = json.get("previous_release").tokenize('.')
        type = "store";
    } else if(flavour.equalsIgnoreCase("debug")){
        (version, feature, bug) = json.get("current_debug").tokenize('.')
        type = "developer";
    } else {
        (version, feature, bug) = json.get("previous_release").tokenize('.')
        type = "developer";
        bug++
    }

    def notes = json.notes
    def release_notes = ""

    while(notes.keySet().contains(version)){
        def notes_version = notes.get(version)
        while(notes_version.keySet().contains(feature)){
            def notes_feature = notes_version.get(feature)
            while(notes_feature.keySet().contains(bug)){
                notes_feature.get(bug).get(type).each{note -> release_notes += " - " + note + "\n"}
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

task generateReleaseNotesStore { task ->
    doLast {
        def fileReleaseNotes = new File(projectDir,'src/main/play/release-notes/en-GB/default.txt')
        fileReleaseNotes.getParentFile().mkdirs()
        if(!fileReleaseNotes.exists()){
            fileReleaseNotes.createNewFile()
        }
        fileReleaseNotes.write generateReleaseNotes("store")
    }
}