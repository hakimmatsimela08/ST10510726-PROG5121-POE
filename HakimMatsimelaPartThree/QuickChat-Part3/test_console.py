"""Run after build.sh/build.bat: python test_console.py (standard library only)."""
import json
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parent
JAR = ROOT / 'dist' / 'QuickChat-Part3.jar'
LOGIN = ['Hakim', 'Matsimela', 'kyl_1', 'Ch&&sec@ke99!', '+27838968976',
         'kyl_1', 'wrong', 'kyl_1', 'Ch&&sec@ke99!']
FLOW = LOGIN + ['3', 'bad', '2', '1', 'invalid', '+27718693002',
    'x' * 251, 'Hi Mike, can you join us for dinner tonight?', '1',
    '+27718693002', 'Hi Keegan, did you receive the payment?', '2', '0',
    '+27718693002', 'Store "this" message', '3', '1', '2', '3']

def run(command, inputs, cwd):
    result = subprocess.run(command, input='\n'.join(inputs) + '\n', text=True,
                            capture_output=True, cwd=cwd, timeout=20)
    assert result.returncode == 0, result.stderr
    return result.stdout

with tempfile.TemporaryDirectory() as directory:
    output = run(['java', '-jar', str(JAR)], FLOW, directory)
    for expected in ['Username or password incorrect', 'Welcome to QuickChat.',
                     'Coming Soon.', 'Message exceeds 250 characters by 1',
                     'Message successfully sent.', 'Message discarded.',
                     'Message successfully stored.', 'Total messages sent: 1',
                     'You have reached your message limit.']:
        assert expected in output, expected
    details = output[output.index('Message successfully sent.'):]
    assert details.index('Message ID:') < details.index('Message Hash:') < details.index('Recipient:') < details.index('Message:')
    files = list(Path(directory).glob('stored_messages/*.json'))
    assert len(files) == 1
    stored = json.loads(files[0].read_text(encoding='utf-8'))
    assert stored['message'] == 'Store "this" message'
    assert stored['messageNumber'] == 2 and stored['status'] == 'STORED'
    (ROOT / 'console-flow-output.txt').write_text(output, encoding='utf-8')
    # Both IDE launch classes must enter Part 2, including the original Part 1 class.
    for entry in ['HakimMokadiMatsimelaPartOne', 'QuickChat']:
        output = run(['java', '-cp', str(JAR), 'hakimmokadimatsimelapartone.' + entry],
                     LOGIN + ['0', '1', '3'], directory)
        assert 'Welcome to QuickChat.' in output
        assert 'Total messages sent: 0' in output
    output = run(['java', '-jar', str(JAR)], [], directory)
    assert 'QuickChat closed.' in output
print('PASS: executable JAR, both IDE entry points, login retry, menu validation, '
      'recipient/length validation, send/discard/store, JSON, limits, totals and end-of-input.')
