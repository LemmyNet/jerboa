#!/bin/bash
set -e

RESOURCE_FILE=$1

ENDPOINT="https://the-federation.info/v1/graphql"
QUERY="{
  plat: thefederation_platform_by_pk(id: 73) {
    nodes: thefederation_nodes {
      name
      agg: thefederation_stats_aggregate {
        aggregate {
          avg {
            users_monthly
          }
        }
      }
    }
  }
}"

BODY="{
	\"query\": \"$QUERY\"
}"

FILTER='
	  .data.plat.nodes                             # Select lemmy nodes
	| sort_by(.agg.aggregate.avg.users_monthly)    # Sort by their average monthly users
	| reverse                                      # Reverse to sort descending
	| limit(10;.[])                                # Take the first few items
	| "    \"" + .name + "\","                     # Wrap in quotes and comma
'

echo 'package com.jerboa' > $RESOURCE_FILE
echo >> $RESOURCE_FILE
echo "val DEFAULT_LEMMY_INSTANCES = arrayOf(" >> $RESOURCE_FILE

curl -sSX POST --data "$(echo $BODY | jq)" "$ENDPOINT" | jq -r "$FILTER" >> $RESOURCE_FILE

echo ")" >> $RESOURCE_FILE
