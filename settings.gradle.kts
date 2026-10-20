rootProject.name = "{{.Inputs.project_name.Value}}"

// server assembles the domain modules, each of which owns its routes and its data
include(":server")
include(":modules:greetings")
{{if eq .Inputs.database.Value "sqlite"}}include(":modules:items")
{{end}}