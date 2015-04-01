import urllib2
import argparse
import re
import os

from multiprocessing import Pool
from multiprocessing import cpu_count

def get_pdf_links_from(url):
    pdflinks = []
    response = urllib2.urlopen(url)
    html = response.read()
    links = re.findall(r'href=\"(.*?)\"', html)
    for link in links:
        if link.endswith('.pdf'):
            pdflinks.append(link)
    return pdflinks

def download_to_path(path, url):
    print 'Downloading file %s to %s' % (url, path)
    pdf = urllib2.urlopen(url)
    with open(os.path.join(path, os.path.basename(url)), 'wb') as f:
        f.write(pdf.read())
    print 'Done downloading file %s' % url

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-d', '--directory', dest='directory', help='The dir',
            required=True)
    parser.add_argument('-u', '--url', dest='url', help='The url',
            required=True)
    args = parser.parse_args()
    pool = Pool(cpu_count() * 2)
    pdflinks = get_pdf_links_from(args.url)
    for pdflink in pdflinks:
        pool.apply_async(download_to_path, args=(args.directory, pdflink))
    pool.close()
    pool.join()
