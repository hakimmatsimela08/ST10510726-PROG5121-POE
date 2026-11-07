"""End-to-end tests of the packaged JAR, Part 1 -> Part 2 -> Part 3, and restart persistence."""
import json
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parent
JAR = ROOT / 'dist/QuickChat-Part3.jar'
LOGIN = ['Hakim', 'Matsimela', 'kyl_1', 'Ch&&sec@ke99!', '+27838968976',
         'kyl_1', 'wrong', 'kyl_1', 'Ch&&sec@ke99!']

def run(inputs, directory):
    p = subprocess.run(['java', '-jar', str(JAR)], input='\n'.join(inputs)+'\n',
                       text=True, capture_output=True, cwd=directory, timeout=20)
    assert p.returncode == 0, p.stderr
    assert p.stdout.count('===User Registration===') == 1
    assert p.stdout.index('=== User Login ===') < p.stdout.index('Welcome to QuickChat.')
    return p.stdout

with tempfile.TemporaryDirectory() as directory:
    folder = Path(directory) / 'stored_messages'
    long_text = 'Where are you? You are late! I have asked you to be on time.'
    output = run(LOGIN + ['4','1',
        '+27834557896','Did you get the cake?','1',
        '+27838884567',long_text,'3',
        '+27834484567','Yohoooo, I am at your gate.','2','0',
        '0838884567','It is dinner time!','3',
        '4','1','2','3','unknown','4','+27838884567','6','7','8','0838884567',
        '9','10','0','3'], directory)
    assert 'Sender: +27838968976' in output
    assert 'Status: STORED' in output and 'Status: SENT' in output
    assert 'Message not found.' in output
    assert 'Total messages sent: 1' in output
    files = list(folder.glob('*.json'))
    assert len(files) == 2
    records = [json.loads(f.read_text()) for f in files]
    target = next(x for x in records if x['message'] == long_text)
    assert all(x['sender'] == '+27838968976' for x in records)
    output2 = run(LOGIN + ['0','4','1','2','3',target['messageID'],
        '4','+27838884567','6','5','missing','5',target['messageHash'],
        '3',target['messageID'],'10','6','0','3'], directory)
    assert 'Loaded 2 stored messages.' in output2
    assert 'Recipient: +27838884567' in output2
    assert 'Message: "' + long_text + '" successfully deleted.' in output2
    assert len(list(folder.glob('*.json'))) == 1
    output3 = run(LOGIN + ['0','4','6','0','3'], directory)
    assert 'Loaded 1 stored messages.' in output3
    assert long_text not in output3
    assert 'It is dinner time!' in output3
    assert 'Total messages sent: 0' in output3
    (ROOT/'part3-console-evidence.txt').write_text(output+'\n--- RESTART ---\n'+output2+'\n--- AFTER DELETION ---\n'+output3)
print('PASS: integrated Parts 1-3, all Part 3 submenu options, local number conversion, restart reload and persistent deletion.')
