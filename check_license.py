import os

# Define the license header to search for
license_header = """/* Copyright 2025 Evan Troxell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
"""

# Define the root directory of your project
root_dir = "/Users/evantroxell/Desktop/TranscendRuins"


# Function to check if a file contains the license header
def file_contains_license(file_path):
    with open(file_path, "r") as file:
        content = file.read()
        return license_header in content


# List to store files missing the license header
files_missing_license = []

# Walk through the project directory
for subdir, _, files in os.walk(root_dir):
    for file in files:
        if file.endswith(".java"):  # Check only Java files
            file_path = os.path.join(subdir, file)
            if not file_contains_license(file_path):
                files_missing_license.append(file_path)

# Print the files missing the license header
if files_missing_license:
    print("Files missing the license header:")
    for file in files_missing_license:
        print(file)
else:
    print("All files contain the license header.")
