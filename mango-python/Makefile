#
# Licensed to Big Data Genomics (BDG) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The BDG licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

define help

Supported targets: prepare, develop, sdist, clean, test, and pypi.

Please note that all build targets require a virtualenv to be active.

The 'prepare' target installs Mango's build requirements into the current virtualenv.

The 'develop' target creates an editable install of Mango and its runtime requirements in the
current virtualenv. The install is called 'editable' because changes to the source code
immediately affect the virtualenv.

The 'clean' target undoes the effect of 'develop'.

The 'test' target runs Mango's unit tests. Set the 'tests' variable to run a particular test, e.g.

	make test tests=src/mango/test/sort/sortTest.py::SortTest::testSort

The 'pypi' target publishes the current commit of Mango to PyPI after enforcing that the working
copy and the index are clean, and tagging it as an unstable .dev build.

endef
export help
help:
	@printf "$$help"

# This Makefile uses bash features like printf and <()
SHELL=bash
python=python
pip=pip
tests=bdgenomics
extras=
mango_version:=$(shell $(python) version.py)
sdist_name:=mango-$(mango_version).tar.gz
current_commit:=$(shell git log --pretty=oneline -n 1 -- $(pwd) | cut -f1 -d " ")
dirty:=$(shell (git diff --exit-code && git diff --cached --exit-code) > /dev/null || printf -- --DIRTY)

# test paths for pyspark
PY4J_ZIP:=$(shell ls -1 "${SPARK_HOME}/python/lib" | grep py4j)
PYTHONPATH:=${SPARK_HOME}/python:${SPARK_HOME}/python/lib/$(PY4J_ZIP):$${PYTHONPATH}
ASSEMBLY_DIR:=../mango-assembly/target
ASSEMBLY_JAR=$(shell ls -1 $(ASSEMBLY_DIR)| grep ^mango-assembly[0-9A-Za-z\_\.-]*\.jar$ | grep -v javadoc | grep -v sources || true)
PYSPARK_SUBMIT_ARGS="--jars $(ASSEMBLY_DIR)/$(ASSEMBLY_JAR) --driver-class-path $(ASSEMBLY_DIR)/$(ASSEMBLY_JAR) pyspark-shell"

green=\033[0;32m
normal=\033[0m\n
red=\033[0;31m


develop:
	$(pip) install -e .$(extras)
clean_develop:
	- $(pip) uninstall -y bdgenomics.mango
	- rm -rf src/*.egg-info

sdist: dist/$(sdist_name)
dist/$(sdist_name):
	@test -f dist/$(sdist_name) && mv dist/$(sdist_name) dist/$(sdist_name).old || true
	$(python) setup.py sdist bdist_egg
	@test -f dist/$(sdist_name).old \
	    && ( cmp -s <(tar -xOzf dist/$(sdist_name)) <(tar -xOzf dist/$(sdist_name).old) \
	         && mv dist/$(sdist_name).old dist/$(sdist_name) \
	         && printf "$(green)No significant changes to sdist, reinstating backup.$(normal)" \
	         || rm dist/$(sdist_name).old ) \
	    || true
clean_sdist:
	- rm -rf dist


test: check_build_reqs
	pushd ../example-files/notebooks \
		&& jupyter nbconvert --to python mango-python-coverage.ipynb \
		&& jupyter nbconvert --to python mango-python-alignment.ipynb \
		&& jupyter nbconvert --to python mango-pileup.ipynb \
		&& jupyter nbconvert --to python mango-python-variants.ipynb
	mkdir -p target
	export PYTHONPATH=$(PYTHONPATH) \
	&& export PYSPARK_SUBMIT_ARGS=$(PYSPARK_SUBMIT_ARGS) \
	&& $(python) -m pytest -vv --junitxml target/pytest-reports/tests.xml $(tests)
	pushd ../example-files/notebooks \
		&& rm mango-python-coverage.py \
		&& rm mango-python-alignment.py \
		&& rm mango-pileup.py \
		&& rm mango-python-variants.py
	exit


pypi: clean clean_sdist check_clean_working_copy
	set -x \
	&& $(python) setup.py egg_info sdist bdist_egg \
	&& twine check dist/* \
	&& twine upload dist/*
clean_pypi:
	- rm -rf build/


clean: clean_develop clean_pypi


check_build_reqs:
    ifndef SPARK_HOME
    	$(error SPARK_HOME is undefined)
    endif
	@$(python) -c 'import pytest' \
		|| ( printf "$(redpip)Build requirements are missing. Run 'make prepare' to install them.$(normal)" ; false )


prepare:
	which $(pip)
	# ipython version required for python 2.7
	$(pip) install -r requirements.txt



check_clean_working_copy:
	@printf "$(green)Checking if your working copy is clean ...$(normal)"
	@git diff --exit-code > /dev/null \
		|| ( printf "$(red)Your working copy looks dirty.$(normal)" ; false )
	@git diff --cached --exit-code > /dev/null \
		|| ( printf "$(red)Your index looks dirty.$(normal)" ; false )
	@test -z "$$(git ls-files --other --exclude-standard --directory)" \
		|| ( printf "$(red)You have are untracked files:$(normal)" \
			; git ls-files --other --exclude-standard --directory \
			; false )

.PHONY: help \
		prepare \
		develop clean_develop \
		sdist clean_sdist \
		test \
		pypi clean_pypi \
		clean \
		check_clean_working_copy \
		check_build_reqs
