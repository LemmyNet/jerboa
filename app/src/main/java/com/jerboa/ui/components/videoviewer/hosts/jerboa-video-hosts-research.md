# Research on video hosts

streamable:
- has OG
- Has Thumbnail
- Has vid
- has width + height
- both have same ratio
- Seems to have expire part of url
- has ld-json
- Expiry seems to be ignored
- example: https://streamable.com/gjs6hc

Conclusion: Rely on default embedded behaviour

sendvid:
- has OG
- has image but no thumbnail
- has vid
- with + height
- short 2h expiry in url
- no ld-json

Conclusion: Custom implementation that just does OGP parsing

redgifs:
- has OG
- no thumbnail in OG, but seems it can be "guessed" -poster.jpg
- has vid
- has width + height
- No expiry part of url
- has ld-json

Conclusion: Use API to get video info

reddit vid:

Conclusion: Too complex to figure out, much information is wrong, leave as is

vimeo:
- has og
- has image
- width + height
- has vid + but not direct link to FILE, links to iframe stuff
- no expiry
- no ld-json
- example: https://vimeo.com/156881088

oembed endpoint https://vimeo.com/api/oembed.json?url=https://vimeo.com/156881088
player endpoint https://player.vimeo.com/video/$id

Conclusion: Not supported

peertube:
- has og
- has image
- has vid + but link to html/js blob
- no expiry
- no ld-json

Conclusion: Not supported