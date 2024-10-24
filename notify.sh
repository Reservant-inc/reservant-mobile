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
MSG="<@&$ROLE_ID>\n## Success mobile apk build on __ $CURRENT_DATE __"
ARTIFACT_URL="https://github.com/Reservant-inc/reservant-mobile/actions/runs/$ARTIFACT_ID"
SUMMARY="Reservant mobile just released new build. The .apk file is available at (Artifacts section):"

curl -H "Content-Type: application/json" \
  -X POST \
  -d '{
    "content": "'"${MSG}"'",
    "embeds": [{
      "title": "Frontend Test Summary",
      "description": "'"${SUMMARY}"'",
      "url": "'"${ARTIFACT_URL}"'",
      "color": 15924992
    }]
  }' \
  "$DISCORD_WEBHOOK_URL"

echo "Summary sent to Discord webhook."