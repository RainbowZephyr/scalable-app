## The JSONs folder holds all JSON structures concerning the inter-apps messaging

#### Each folder is named after its app
#### Each folder contains the json format of request & response for each service provided by the app

Request: JSONs/App/app_service_request.json

Response: JSONs/App/app_service_response.json

the following conventions are used:
  1. JSON Objects & keys are written in small alphabets with `_` as separator.
  2. Values are left **empty** if they are **app-dependent**.
  
    if value is a string -> `""`, 
    if number -> `0` 
    
    But **static values** for a service are written.
  3. Filenames are written as follows `appName (camelCase) _ service _ request or response].json`. 
  Example: `exampleApp_service_to_be_offered_request.json` & `appName_service_to_be_offered_response.json` .
  4. Files with `_` at the beginning, means that it should be available in all apps ex. `_service_request.json` .
