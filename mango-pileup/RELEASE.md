# Release on npm
JupyterLab requires an npm version to be installed.
To release a new version of bdgenomics.mango.pileup on npm:

- Update the frontend `version` in `bdgenomics/mango/js/package.json`
and `bdgenomics/mango/pileup/_version.py`.
Make sure these versions match. Commit this change.
- cd into `bdgenomics/mango/js/`
- Run `scripts/publish.sh`.
- Run `npm publish`.
- Push to github.

If you are publishing a beta version, run:

- `npm publish --tag beta`


# Release on PyPI

To release a new version of bdgenomics.mango.pileup on PyPI:

Update version in _version.py (set release version, remove 'dev')
Also, update the npm frontend version in _version.py to match (TODO LINK to npm)
Update version in bdgenomics/mango/pileup/js/package.json
make clean
make sdist
git add and git commit
make sdist
twine upload dist/*.tar.gz
git tag -a X.X.X -m 'comment'

Update _version.py (add 'dev' and increment minor)
git add and git commit
git push
git push --tags
