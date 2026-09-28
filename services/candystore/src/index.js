import dotenv from "dotenv";
import express from "express";
import mysql from "mysql2/promise";
import path from "path";
import { fileURLToPath } from "url";
import winston from "winston";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
dotenv.config({ path: path.resolve(__dirname, "../../../.env") });

const logger = winston.createLogger({
  level: "info",
  format: winston.format.combine(winston.format.timestamp(), winston.format.json()),
  transports: [new winston.transports.Console()],
});

const pool = mysql.createPool({
  host: process.env.DB_HOST || "127.0.0.1",
  port: Number(process.env.DB_PORT || 3306),
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME || "cine",
  waitForConnections: true,
  connectionLimit: 5,
  decimalNumbers: true,
});

const app = express();
const port = Number(process.env.PORT || 3002);

app.use((req, res, next) => {
  const requestId = req.header("x-request-id") || crypto.randomUUID();
  req.requestId = requestId;
  res.setHeader("x-request-id", requestId);
  const started = Date.now();
  res.on("finish", () => {
    logger.info({
      service: "candystore",
      requestId,
      method: req.method,
      path: req.path,
      status: res.statusCode,
      ms: Date.now() - started,
    });
  });
  next();
});

app.get("/health", (_req, res) => {
  res.json({ status: "UP", service: "candystore" });
});

app.get("/candy", async (req, res) => {
  try {
    const [rows] = await pool.query("CALL sp_list_candy()");
    const list = (Array.isArray(rows[0]) ? rows[0] : rows).map((row) => ({
      id: row.id,
      name: row.name,
      description: row.description,
      price: Number(row.price),
    }));
    logger.info({ service: "candystore", requestId: req.requestId, count: list.length });
    res.json(list);
  } catch (error) {
    logger.error({ service: "candystore", requestId: req.requestId, message: error.message });
    res.status(500).json({ code: "1", message: "No se pudo leer la dulcería" });
  }
});

app.listen(port, () => {
  logger.info({ service: "candystore", message: `escuchando en ${port}` });
});
