import { mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { resolve } from "node:path";
import { gzipSync } from "node:zlib";
import ts from "typescript";
import * as THREE from "three";

const [source, output] = process.argv.slice(2);
if (!source || !output) {
  throw new Error("Usage: node export-pleos-models.mjs <figma-make/src> <assets/pleos-models>");
}
mkdirSync(output, { recursive: true });

function runModel(sourceName, from, until, returned, args) {
  const file = readFileSync(resolve(source, sourceName), "utf8");
  const start = file.indexOf(from);
  const end = file.indexOf(until, start);
  if (start < 0 || end < 0) throw new Error("Model source changed: " + sourceName);
  const code = ts.transpileModule(file.slice(start, end), {
    compilerOptions: { target: ts.ScriptTarget.ES2022 },
  }).outputText;
  return new Function("THREE", ...Object.keys(args), code + "\nreturn " + returned + ";")(
    THREE,
    ...Object.values(args),
  );
}

function exportModel(name, root, highlights = {}) {
  root.updateMatrixWorld(true);
  const batches = [];
  root.traverse((mesh) => {
    if (!(mesh instanceof THREE.Mesh) || !mesh.visible) return;
    const positions = mesh.geometry.getAttribute("position");
    const normals = mesh.geometry.getAttribute("normal");
    if (!positions || !normals || Array.isArray(mesh.material)) return;
    const index = mesh.geometry.getIndex();
    const normalMatrix = new THREE.Matrix3().getNormalMatrix(mesh.matrixWorld);
    const color = mesh.material.color.getHex(THREE.SRGBColorSpace);
    const part = Object.entries(highlights).findIndex(([, meshes]) => meshes.includes(mesh)) + 1;
    let placement = mesh.geometry.type === "CircleGeometry" ? 5 : 0;
    if (name.startsWith("village-") && mesh.parent === root) {
      const shape = mesh.geometry.parameters;
      if (mesh.geometry.type === "CylinderGeometry" && shape.radiusTop >= 5.5 && mesh.position.y <= 0) {
        placement = shape.radiusTop > 7 ? 6 : shape.radiusTop > 6 ? 7 : 8;
        if (mesh.position.y === 0 && color === 0x385b5d) placement = 9;
      }
      if (mesh.geometry.type === "BoxGeometry" && shape.height <= 0.05 &&
          Math.max(shape.width, shape.depth) >= 8 && mesh.position.y < 0.3) placement = 10;
    }
    if (mesh.geometry.type === "SphereGeometry" && name.startsWith("village-")) {
      if (color === 0xcbd5cc && mesh.material.opacity === 0.32) {
        placement = 11 + Math.round((mesh.geometry.parameters.radius - 0.12) / 0.04);
      }
      if (color === 0xffffff && mesh.material.opacity === 0.88) {
        placement = mesh.parent.position.x < 0 ? 14 : 15;
      }
    }
    batches.push({ mesh, positions, normals, index, color, normalMatrix, tag: part || placement });
  });
  const vertexCount = batches.reduce((sum, b) => sum + (b.index?.count ?? b.positions.count), 0);
  const buffer = Buffer.alloc(4 + 28 * vertexCount);
  buffer.writeUInt32LE(vertexCount, 0);
  const point = new THREE.Vector3();
  const normal = new THREE.Vector3();
  let offset = 4;
  for (const b of batches) {
    const count = b.index?.count ?? b.positions.count;
    for (let i = 0; i < count; i++) {
      const j = b.index?.getX(i) ?? i;
      point.fromBufferAttribute(b.positions, j).applyMatrix4(b.mesh.matrixWorld);
      normal.fromBufferAttribute(b.normals, j).applyNormalMatrix(b.normalMatrix);
      for (const n of [point.x, point.y, point.z, normal.x, normal.y, normal.z]) {
        buffer.writeFloatLE(Math.round(n * 100000) / 100000, offset);
        offset += 4;
      }
      buffer.writeUInt8(b.color >> 16, offset++);
      buffer.writeUInt8((b.color >> 8) & 255, offset++);
      buffer.writeUInt8(b.color & 255, offset++);
      buffer.writeUInt8(b.tag, offset++);
    }
  }
  const data = gzipSync(buffer, { level: 9 });
  writeFileSync(resolve(output, name + ".meshdata"), data);
  console.log(name + ": " + vertexCount / 3 + " triangles, " + data.length + " bytes");
}

for (let stage = 0; stage < 12; stage++) {
  const root = new THREE.Group();
  runModel("ThreeVillage.tsx", "const colors", "export default function",
    "buildEra(root, stage, animations)", { root, stage, animations: [] });
  exportModel("village-" + stage, root);
}

const scene = new THREE.Scene();
const { highlights } = runModel("ThreeVehicle.tsx", "    const car = new THREE.Group();",
  "    const targets:", "({ highlights })", { scene });
exportModel("vehicle", scene, highlights);

