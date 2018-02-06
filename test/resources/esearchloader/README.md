# Elastic Search File Generation

Run *GenerateOffenderESFile* this will generate *es-test-data.txt* in the resources **output** directory as a sibling file to the read csv file

By default this will read *uk-500.csv* file. Amend the code to read another file.

Assuming AWS Elastic search only allows 10Mb upload files, then large files will need to split into chunks. To do that run:

`split -a 3 -l 20000  es-test-data.txt es-test-data-chunk-`

Each file can then be upload using the following:


`find . -iname 'es-test-data-chunk*' -execdir url -H "Content-Type: application/json" -XPOST 'https://search-offender-amjj6s2g2jpanondipkd4nm57y.eu-west-2.es.amazonaws.com/offender/_bulk?pretty&refresh' --data-binary @{} \;`
