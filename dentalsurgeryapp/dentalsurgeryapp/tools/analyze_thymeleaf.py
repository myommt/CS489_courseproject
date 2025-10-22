import re
import sys
p=r'c:\Users\zaimy\OneDrive\Documents\Git\MIUCompro\CS489\CourseProject\dentalsurgeryapp\dentalsurgeryapp\src\main\resources\templates\secured\sysadmin\usermgt\newuser.html'
with open(p,'r',encoding='utf-8') as f:
    lines=f.readlines()

pattern=re.compile(r'(\$\{[^}]*\}|\#\{[^}]*\})')
for i,l in enumerate(lines, start=1):
    for m in pattern.finditer(l):
        expr=m.group(0)
        print(f"{i}: {expr}")

# Also print any lines containing unmatched ${ or #{ without closing }
for i,l in enumerate(lines, start=1):
    if '${' in l and '}' not in l:
        print(f"UNMATCHED ${'{'} on line {i}: {l.strip()}")
    if '#{' in l and '}' not in l:
        print(f"UNMATCHED #{'{'} on line {i}: {l.strip()}")
