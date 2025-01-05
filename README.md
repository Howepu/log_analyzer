# Log Analyzer

The **Log Analyzer** is a utility that processes NGINX log files to provide insightful statistics about web traffic. It can parse both local log files and logs accessible via a URL. The program is designed to make log analysis easy by automating tasks like counting requests, identifying frequent resources, calculating average response sizes, and more.

## Features

- **Count the total number of requests**
- **Identify the most requested resources**
- **Determine the most common HTTP response codes**
- **Calculate the average response size**
- **Calculate the 95th percentile of response sizes**

## Requirements

- Java 21 or higher
- Maven
- Internet access for URL-based log files (if necessary)

## Installation

1. Clone the repository or download the source code.
2. Install the analyzer script to easily run the utility from the command line.

```bash
chmod +x install.sh && ./install.sh
```

3. The program will be available as the analyzer command in your terminal.

## Usage
The program takes the following command-line arguments:

- --path: Path to one or more NGINX log files (can be a local path or URL).
- --from: Optional start date in ISO8601 format (e.g., 2024-08-31).
- --to: Optional end date in ISO8601 format (e.g., 2024-09-01).
- --format: Optional output format, either markdown or adoc.
- --filter-field: Optional field to filter logs by (e.g., agent, method).
- --filter-value: Optional value to match in the --filter-field (e.g., "Mozilla*", "GET").

## Examples:
### Basic usage:
```bash
analyzer --path src/main/resources/logs.log --format adoc
```
```bash
analyzer --path https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs --format adoc
```
```bash
analyzer --path src/main/resources/logs.log --from 2014-01-02 --to 2020-02-05 --format markdown
```

### Filter by specific field:
```bash
analyzer --path logs/2024* --filter-field agent --filter-value "Mozilla*"
```
This command filters logs based on the agent field, showing only entries where the user-agent starts with "Mozilla".

```bash
analyzer --path logs/2024* --filter-field method --filter-value "GET"
```
This command filters logs based on the method field, showing only entries with the HTTP method "GET".




## Output Format
The program will output the analysis in either markdown or adoc format, depending on the selected output format. Here is an example of the output:

- [Markdown](analyzer_output.md)

- [Asciidoc](analyzer_output.adoc)

## How It Works
The program reads the NGINX log files either from local paths or remote URLs.
If specified, the program filters logs by date range (from and to parameters).
The program then parses each log entry and transforms it into a structured object.
Statistics are collected during the parsing process in a single pass through the data.
Finally, the results are formatted into a markdown or AsciiDoc report.

## Limitations & Tips
Log Format: Ensure that the logs conform to the standard NGINX log format for proper parsing.
Memory Efficiency: The program uses stream-based processing to handle large log files without loading them entirely into memory.
Data Processing: All statistics are calculated in a single pass over the data, making the program efficient.
Date Handling: Use Java's modern time API for date filtering.
Statistics Collection: Use collections like lists or maps to collect statistics efficiently.
