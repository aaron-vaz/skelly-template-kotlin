// Every project gets the base package as its group; a module can append its own name to it
allprojects {
    group = "{{.Inputs.package.Value}}"
}
