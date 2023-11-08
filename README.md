# ExamHelperPublic
Java project for cheating on online tests. It can run in the background without generating visible on-screen logs. Useful for tests that capture screen.
### How it works
- It captures selected screen area(exam question area). Using mouse clicks it draws an invisible rectangle around a question.
- The question screenshot is passed through an ORC to capture text from image.
- The captured text is pass through a text formatter to format the text.
- The formated text is then send to a default configured AI to generate answer.
- The generated answer is pass through a text formatter to format the answer.
- The formated text is then send to a text to speech to dictate the answer to the lister.
  Can be configured to print answer to console instead of dictating in cases where exam does not capture user screen.

NB: Quality of the answers is dictated by the AI that is used. Can be configured to use various AI's to generate answers.
# Usage
See support section.

# Contribution
We welcome contributions to this project. Please submit pull requests for any changes you would like to make.

# License
This project is licensed under the GNU General Public License (GPL): https://www.gnu.org/licenses/gpl-3.0.en.html
For commercial use of the software see support section.

# Support
We can offer support for a small fee.
Some of the support that we can offer are:
- Installation and usage training.
- Configure more AI options.
- Train AI's on your data.
- Adapt the project to solve a different problem.
- Any AI development and training.
- Change of license for commercial usage.
- Any unrelated java and AI projects.

# Contact
 rkip0744@gmail.com