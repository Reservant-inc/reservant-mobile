#!/bin/bash

if [ -z "$DISCORD_WEBHOOK_URL" ]; then
    echo "Error: DISCORD_WEBHOOK_URL environment variable is not set."
    exit 1
fi

if [ -z "$ARTIFACT_ID" ]; then
    echo "Error: ARTIFACT_ID environment variable is not set."
    exit 1
fi

CURRENT_DATE=$(TZ=Etc/GMT-2 date +"%Y-%m-%d %H:%M:%S")
ROLE_ID="1174437656688607353"
MSG="<@&$ROLE_ID>\n## Mobile apk build success on __ $CURRENT_DATE __"
SUMMARY="Passed: **$PASSED_COUNT**\nErrors: **$ERRORS_COUNT**"
ARTIFACT_URL="https://github.com/Reservant-inc/reservant-mobile/actions/artifacts/$ARTIFACT_ID"

curl -H "Content-Type: application/json" \
  -X POST \
  -d '{
    "content": "'"${MSG}"'",
    "embeds": [{
      "title": "Frontend Test Summary",
      "description": "'"${SUMMARY}"'",
      "URL": ""
      "color": 15924992
    }]
  }' \
  "$DISCORD_WEBHOOK_URL"

echo "Summary sent to Discord webhook."


echo "Log file sent to Discord webhook."

