# Copyright 2025 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Variables for Google Cloud deployment
serviceName := "mn-mcp-server"
region := "europe-west1"

# Default task to show a welcome message
default:
    echo 'Welcome to the Micronaut MCP Server project!'
    echo 'You can use "just run" to run the server locally,'
    echo 'and "just deploy" to deploy the service to Cloud Run from source.'

# Run the application locally
run:
    ./gradlew run

# Run the MCP inspector
inspector:
    npx @modelcontextprotocol/inspector

# Deploy the service to Google Cloud Run from source
deploy:
    @echo "Deploying the service to Cloud Run from source..."
    gcloud run deploy {{serviceName}} \
      --source . \
      --platform managed \
      --region {{region}} \
      --allow-unauthenticated


